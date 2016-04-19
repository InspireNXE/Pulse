package org.inspirenxe.pulse.console;

import org.inspirenxe.pulse.SpongeGame;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.util.Tristate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Console implements ConsoleSource {

    public Console() {

    }

    @Override
    public String getName() {
        return SpongeGame.ECOSYSTEM_NAME;
    }

    @Override
    public String getIdentifier() {
        return SpongeGame.ECOSYSTEM_IDENTIFIER;
    }

    @Override
    public Set<Context> getActiveContexts() {
        return null;
    }

    @Override
    public void sendMessage(Text message) {

    }

    @Override
    public MessageChannel getMessageChannel() {
        return null;
    }

    @Override
    public void setMessageChannel(MessageChannel channel) {

    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return null;
    }

    @Override
    public SubjectCollection getContainingCollection() {
        return null;
    }

    @Override
    public SubjectData getSubjectData() {
        return null;
    }

    @Override
    public SubjectData getTransientSubjectData() {
        return null;
    }

    @Override
    public boolean hasPermission(Set<Context> contexts, String permission) {
        return false;
    }

    @Override
    public Tristate getPermissionValue(Set<Context> contexts, String permission) {
        return null;
    }

    @Override
    public boolean isChildOf(Set<Context> contexts, Subject parent) {
        return false;
    }

    @Override
    public List<Subject> getParents(Set<Context> contexts) {
        return null;
    }
}
