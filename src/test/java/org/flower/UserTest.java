package org.flower;

import org.flower.risibank.exceptions.RisibankException;
import org.flower.risibank.media.MediaPartial;
import org.flower.risibank.user.User;
import org.flower.risibank.user.UserMediaFlow;
import org.flower.risibank.user.UserStat;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class UserTest
{

    @Test
    public void shouldAnswerWithTrue() {

        try {

            // we should compare the given data
            // with https://risibank.fr/top-contributeurs

            LinkedHashMap<String, UserStat> topUsers = User.topUsers();
            System.out.println(topUsers.keySet());
            String usernameTop1User = topUsers.keySet().toArray(new String[0])[0];
            User topUser = User.byUsername(usernameTop1User);
            System.out.println(topUser);
            User sameTopUser = User.byId(topUser.getId());
            System.out.println(sameTopUser);
            System.out.println(topUser.isPartial());

            UserMediaFlow mediaFlow = topUser.getMedias();

            // should get the portfolio of the top contributor
            while (mediaFlow.hasNext()) {
                List<MediaPartial> mediaPartialList = mediaFlow.next();

                System.out.println(Arrays.toString(mediaPartialList.stream().map(MediaPartial::getSource).toArray()));
            }

        } catch (RisibankException e) {
            e.getCause().printStackTrace();
        }
    }
}
