package uk.joshiejack.simplyseasons.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import uk.joshiejack.simplyseasons.SimplySeasons;

public class SSLanguage extends LanguageProvider {
    public SSLanguage(DataGenerator gen) {
        super(gen, SimplySeasons.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("simplyseasons.hud", "%1$s %2$s");
        add("simplyseasons.spring", "Spring");
        add("simplyseasons.summer", "Summer");
        add("simplyseasons.autumn", "Autumn");
        add("simplyseasons.winter", "Winter");
        add("simplyseasons.wet", "Wet Season");
        add("simplyseasons.dry", "Dry Season");
    }
}