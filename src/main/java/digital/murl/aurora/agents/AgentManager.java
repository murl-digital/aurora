package digital.murl.aurora.agents;

import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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

    static {
        activeAgents = new ConcurrentHashMap<>();
        agentCache = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .build();
    }

    @Nullable
    public static String createAgent(String id, String agentName, Map<String, Object> params) {
        Agent agent = getInstance(agentName);
        if (agent == null) return null;

        agent.init(params);
        activeAgents.put(id, agent);

        return id;
    }

    @Nullable
    public static String createAgent(String agentName, Map<String, Object> params) {
        Agent agent = getInstance(agentName);
        if (agent == null) return null;

        agent.init(params);
        String id = generateRandomString();
        activeAgents.put(id, agent);

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
        boolean keep = actions.get(actionName).apply(agent, params);

        switch (Objects.requireNonNull(cacheBehavior)) {
            case EPHEMERAL:
                break;
            case PERSISTENT:
                id = generateRandomString();
                activeAgents.put(id, agent);
                break;
            case NORMAL:
                id = generateRandomString();
                if (keep)
                    activeAgents.put(id, agent);
                else
                    agentCache.put(id, agent);
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
