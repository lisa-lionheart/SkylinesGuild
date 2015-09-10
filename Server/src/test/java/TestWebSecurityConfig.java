import me.croxford.SkylinesGuild.WebSecurityConfig;
import me.croxford.SkylinesGuild.model.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * Created by Lisa on 09/09/2015.
 */
public class TestWebSecurityConfig {

    WebSecurityConfig ws;


    @Before
    public void Setup() {
        ws = new WebSecurityConfig();
    }


    @Test
    public void correctlyGetsSteamUser() {

//        String openId = "http://steamcommunity.com/openid/id/76561197960546539";
//
//        User user = (User)ws.loadUserByUsername(openId);
//
//        Assert.notNull(user);


    }



}
