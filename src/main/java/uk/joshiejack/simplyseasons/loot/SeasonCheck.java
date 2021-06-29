package uk.joshiejack.simplyseasons.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;

public class SeasonCheck implements ILootCondition {
    public static final LootConditionType SEASON = new LootConditionType(new Serializer());
    private final SeasonPredicate predicate;

    private SeasonCheck(SeasonPredicate predicate) {
        this.predicate = predicate;
    }

    @Nonnull
    @Override
    public LootConditionType getType() {
        return SEASON;
    }

    @Override
    public boolean test(LootContext ctx) {
        Vector3d v3d = ctx.getParamOrNull(LootParameters.ORIGIN);
        return v3d != null && this.predicate.matches(ctx.getLevel(), v3d.x, v3d.y, v3d.z);
    }

    public static class Serializer implements ILootSerializer<SeasonCheck> {
        @Override
        public void serialize(JsonObject jsonObject, SeasonCheck check, @Nonnull JsonSerializationContext ctx) {
            jsonObject.add("predicate", check.predicate.serializeToJson());
        }

        @Nonnull
        public SeasonCheck deserialize(JsonObject jsonObject, @Nonnull JsonDeserializationContext ctx) {
            SeasonPredicate check = SeasonPredicate.fromJson(jsonObject.get("predicate"));
            return new SeasonCheck(check);
        }
    }
}
