package lab.common.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import lab.common.commands.AbstractCommand;

public class CommandManager<K> {

    private Map<K, AbstractCommand> commands;

    public CommandManager() {
    }

    public CommandManager(Map<K, AbstractCommand> commands) {
        this.commands = commands;
    }

    public void add(K key, AbstractCommand command) {
        commands.put(key, command);
    }

    public AbstractCommand get(Object key) {
        return commands.get(key);
    }

    public AbstractCommand getOrDefault(Object key, AbstractCommand defaultValue) {
        return commands.getOrDefault(key, defaultValue);
    }

    public void setCommands(Map<K, AbstractCommand> commands) {
        this.commands = commands;
    }

    public boolean containsValue(AbstractCommand command) {
        return commands.containsValue(command);
    }

    public boolean containsKey(Object key) {
        return commands.containsKey(key);
    }

    public Collection<AbstractCommand> getCommands() {
        return Collections.unmodifiableCollection(commands.values());
    }

}
