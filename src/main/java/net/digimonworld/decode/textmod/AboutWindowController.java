package net.digimonworld.decode.textmod;

import javafx.event.ActionEvent;

public class AboutWindowController {
    private static final String LICENSE_URL = "https://github.com/Thertzlor/DecodeTextMod/blob/master/LICENSE";
    private static final String GITHUB_URL = "https://github.com/Thertzlor/DecodeTextMod";
    private static final String DISCORD_URL = "https://discord.gg/AeRYeGJF2P";
    private static final String THIRD_PARTY_LICENSE_URL = "https://github.com/Thertzlor/DecodeTextMod/blob/master/THIRD-PARTY-NOTICE";
    
    private static final String PATREON_URL = "https://patreon.com/sydmontague";
    private static final String PAYPAL_URL = "https://paypal.me/sydmontague";
    
    public void clickLicense(ActionEvent e) {
        DecodeTextMod.getInstance().getHostServices().showDocument(LICENSE_URL);
    }
    
    public void clickThirdPartyLicense(ActionEvent e) {
        DecodeTextMod.getInstance().getHostServices().showDocument(THIRD_PARTY_LICENSE_URL);
    }
    
    public void clickGitHub(ActionEvent e) {
        DecodeTextMod.getInstance().getHostServices().showDocument(GITHUB_URL);
    }
    
    public void clickDiscord(ActionEvent e) {
        DecodeTextMod.getInstance().getHostServices().showDocument(DISCORD_URL);
    }
    
    public void clickPatreon(ActionEvent e) {
        DecodeTextMod.getInstance().getHostServices().showDocument(PATREON_URL);
    }
    
    public void clickPayPal(ActionEvent e) {
        DecodeTextMod.getInstance().getHostServices().showDocument(PAYPAL_URL);
    }
}
