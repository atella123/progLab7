package lab.common.commands.datacommands;

public enum RegisterCommandFlags {
    LOGIN,
    REGISTER;

    public static RegisterCommandFlags getFlag(String s) {
        if (s.matches("[Ll](ogin)")) {
            return LOGIN;
        }
        if (s.matches("[Rr](egister)")) {
            return REGISTER;
        }
        throw new IllegalArgumentException(
                String.format("No enum constant %s.%s", RegisterCommandFlags.class.toString(), s));
    }
}
