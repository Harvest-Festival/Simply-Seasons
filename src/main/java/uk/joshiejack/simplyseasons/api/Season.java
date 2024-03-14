package uk.joshiejack.simplyseasons.api;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum Season implements StringRepresentable {
    SPRING, SUMMER, AUTUMN, WINTER, WET, DRY;

    public static final Codec<Season> CODEC = StringRepresentable.fromEnum(Season::values);

    public static final Season[] MAIN = new Season[4];

    static {
        System.arraycopy(values(), 0, MAIN, 0, 4);
    }

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}