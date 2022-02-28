package digital.murl.aurora.agents;

import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import digital.murl.aurora.Aurora;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AgentManager {
    private static final ConcurrentHashMap<String, Agent> activeAgents;
    private static final Cache<String, Agent> agentCache;
    private static final RemovalListener<String, Agent> listener = r -> {
        if (r.wasEvicted())
            r.getValue().cleanup();
    };

    static {
        activeAgents = new ConcurrentHashMap<>();
        agentCache = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .removalListener(listener)
            .build();
    }

    @Nullable
    public static String createAgent(String id, String agentName, Map<String, Object> params) {
        return putAgent(id, agentName, params);
    }

    @Nullable
    public static String createAgent(String agentName, Map<String, Object> params) {
        return putAgent(generateRandomString(), agentName, params);
    }

    @Nullable
    private static String putAgent(String id, String agentName, Map<String, Object> params) {
        CacheBehavior cacheBehavior = AgentRegistrar.getCacheBehavior(agentName);
        if (cacheBehavior == null)
            return null;
        if (cacheBehavior == CacheBehavior.EPHEMERAL)
            throw new IllegalArgumentException("You can't create an agent with an ephemeral cache behavior");

        Agent agent = getInstance(agentName);
        if (agent == null) return null;

        agent.init(params);
        switch (cacheBehavior) {
            case PERSISTENT:
                activeAgents.put(id, agent);
            case NORMAL:
                agentCache.put(id, agent);
        }

        return id;
    }

    public static String executeAgentAction(String agentName, String actionName, Map<String, Object> params) {
        Agent agent = getInstance(agentName);
        Map<String, Action> actions = AgentRegistrar.getAgentActions(agentName);
        if (agent == null || actions == null)
            return null;
        if (!actions.containsKey(actionName))
            return null;

        CacheBehavior cacheBehavior = AgentRegistrar.getCacheBehavior(agentName);

        String id = "";
        agent.init(params);
        Action.Result result = actions.get(actionName).apply(agent, params);

        switch (Objects.requireNonNull(cacheBehavior)) {
            case EPHEMERAL:
                break;
            case PERSISTENT:
                id = generateRandomString();
                activeAgents.put(id, agent);
                break;
            case NORMAL:
                id = generateRandomString();
                switch (result) {
                    case DEFAULT:
                    case ACTIVE:
                        activeAgents.put(id, agent);
                        break;
                    case INACTIVE:
                        agentCache.put(id, agent);
                        break;
                }
        }

        return id;
    }

    public static String executeAgentAction(String id, String agentName, String actionName, Map<String, Object> params) {
        Agent agent;
        boolean wasCached = false;
        if (!agentCache.asMap().containsKey(id) && !activeAgents.containsKey(id)) {
            agent = getInstance(agentName);
            if (agent == null)
                return null;
            agent.init(params);
        } else {
            agent = agentCache.getIfPresent(id);
            if (agent == null)
                agent = activeAgents.get(id);
            else
                wasCached = true;
        }

        Map<String, Action> actions = AgentRegistrar.getAgentActions(agentName);
        CacheBehavior cacheBehavior = AgentRegistrar.getCacheBehavior(agentName);
        if (actions == null || !actions.containsKey(actionName))
            return null;
        if (cacheBehavior == null)
            return null;

        Action.Result result = actions.get(actionName).apply(agent, params);

        if (cacheBehavior == CacheBehavior.EPHEMERAL)
            // pretty weird that a transport would call this method for an ephemeral but whatever
            return id;

        if (cacheBehavior == CacheBehavior.PERSISTENT) {
            if (wasCached) {
                // this should never happen, but just in case
                agentCache.invalidate(id);
                activeAgents.put(id, agent);
            }

            return id;
        }

        switch (result) {
            case ACTIVE:
                if (wasCached) {
                    agentCache.invalidate(id);
                    activeAgents.put(id, agent);
                }
                break;
            case INACTIVE:
                if (!wasCached) {
                    activeAgents.remove(id);
                    agentCache.put(id, agent);
                }
                break;
            case DEFAULT:
                // we don't have to do anything
                break;
        }

        return id;
    }

    @Nullable
    private static Agent getInstance(String agentName) {
        Agent agent;
        try {
            agent = AgentRegistrar.getAgent(agentName);
        } catch (InstantiationException | IllegalAccessException e) {
            Aurora.logger.severe(Throwables.getStackTraceAsString(e));
            return null;
        }

        if (agent == null) {
            Aurora.logger.warning(String.format("Attempted to create an agent of type %s that doesn't exist", agentName));
            return null;
        }
        return agent;
    }

    // https://medium.com/beingcoders/ways-to-generate-random-string-in-java-6d3b1d964c02
    private static String generateRandomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }
}
