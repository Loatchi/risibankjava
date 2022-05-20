package org.flower;

import org.flower.risibank.exceptions.RisibankException;
import org.flower.risibank.user.User;
import org.flower.risibank.user.UserStat;
import org.junit.Test;

import java.util.LinkedHashMap;

public class UserTest
{

    @Test
    public void shouldAnswerWithTrue() {

        try {

            //we should compare the given data with
            // to compare with https://risibank.fr/top-contributeurs

            LinkedHashMap<String, UserStat> topUsers = User.topUsers();
            System.out.println(topUsers.keySet());
            String usernameTop1User = topUsers.keySet().toArray(new String[0])[0];
            User topUser = User.byUsername(usernameTop1User);
            System.out.println(topUser);
            User sameTopUser = User.byId(topUser.getId());
            System.out.println(sameTopUser);

        } catch (RisibankException e) {
            System.out.println(e.getCause());
        }
    }
}
