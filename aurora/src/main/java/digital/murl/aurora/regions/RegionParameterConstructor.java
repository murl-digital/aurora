package digital.murl.aurora.regions;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface RegionParameterConstructor {
    Result regionConstructor(Player sender, String[] params);
    class Result {
        public static final Result SUCCESS = new Result(true);
        public static final Result FAILURE = new Result(false);
        public static final Result WRONG_SYNTAX = new Result(false,"Incorrect syntax for region constructor.");

        public final boolean success;
        public final String output;

        public Result(boolean success) {
            this.success = success;
            this.output = "";
        }

        public Result(boolean success, String output) {
            this.success = success;
            this.output = output;
        }
    }
}
