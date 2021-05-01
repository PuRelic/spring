package net.purelic.spring.commands.discord;

import cloud.commandframework.Command;
import cloud.commandframework.jda.JDA4CommandManager;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.purelic.commons.Commons;
import net.purelic.spring.commands.DiscordCommand;
import net.purelic.spring.commands.parsers.DiscordUser;
import net.purelic.spring.commands.parsers.GuildUser;
import net.purelic.spring.discord.Role;
import net.purelic.spring.utils.DiscordUtils;

import java.util.concurrent.ExecutionException;

public class TopReferrersCommand implements DiscordCommand {

    @Override
    public Command.Builder<DiscordUser> getCommandBuilder(JDA4CommandManager<DiscordUser> mgr) {
        return mgr.commandBuilder("topreferrers")
            .senderType(GuildUser.class)
            .permission(Role.staff())
            .handler(c -> {
                GuildUser sender = (GuildUser) c.getSender();
                MessageChannel channel = sender.getChannel();

                // Get top 10 referrers
                Query query = Commons.getFirestore().collection("discord_users")
                    .orderBy("referrals", Query.Direction.DESCENDING)
                    .limit(10);

                ApiFuture<QuerySnapshot> future = query.get();

                try {
                    QuerySnapshot querySnapshot = future.get();

                    if (querySnapshot.isEmpty()) return;

                    String description = "";

                    for (int i = 0; i < querySnapshot.getDocuments().size(); i++) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(i);
                        String userId = documentSnapshot.getId();
                        long referrals = documentSnapshot.getLong("referrals");
                        String mention = DiscordUtils.getGuild().getMemberById(userId).getAsMention();

                        String prefix = (i == 0 ? "" : "\n") + (i + 1) + ". ";
                        String suffix = " - " + referrals;
                        description += prefix + mention + suffix;
                    }

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Top Referrers", null);
                    eb.setDescription(description);

                    channel.sendMessage(eb.build()).queue();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
    }

}
