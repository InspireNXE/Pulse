package org.inspirenxe.pulse.save;

import static com.google.common.base.Preconditions.checkNotNull;

import org.inspirenxe.pulse.SpongeGame;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.SerializationBehavior;
import org.spongepowered.api.world.SerializationBehaviors;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Collection;

public final class ServerWorldCreationSettings implements WorldCreationSettings {
    protected String id, name;
    protected DimensionType dimensionType;
    protected GeneratorType generatorType;
    protected GameMode gameMode;
    protected SerializationBehavior serializationBehavior;
    protected boolean isEnabled, loadOnStartup, doesKeepSpawnLoaded, generateSpawnOnLoad, usesMapFeatures, isHardcore, commandsAllowed,
            doesGenerateBonusChest, isPvpEnabled;
    protected long seed;

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public boolean loadOnStartup() {
        return this.loadOnStartup;
    }

    @Override
    public boolean doesKeepSpawnLoaded() {
        return this.doesKeepSpawnLoaded;
    }

    @Override
    public boolean doesGenerateSpawnOnLoad() {
        return this.generateSpawnOnLoad;
    }

    @Override
    public long getSeed() {
        return seed;
    }

    @Override
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Override
    public GeneratorType getGeneratorType() {
        return this.generatorType;
    }

    @Override public Collection<WorldGeneratorModifier> getGeneratorModifiers() {
        return null;
    }

    @Override
    public boolean usesMapFeatures() {
        return this.usesMapFeatures;
    }

    @Override
    public boolean isHardcore() {
        return this.isHardcore;
    }

    @Override
    public boolean areCommandsAllowed() {
        return this.commandsAllowed;
    }

    @Override
    public boolean doesGenerateBonusChest() {
        return this.doesGenerateBonusChest;
    }

    @Override
    public DimensionType getDimensionType() {
        return this.dimensionType;
    }

    @Override
    public boolean isPVPEnabled() {
        return this.isPvpEnabled;
    }

    @Override public DataContainer getGeneratorSettings() {
        return null;
    }

    public SerializationBehavior getSerializationBehavior() {
        return this.serializationBehavior;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static final class Builder implements WorldCreationSettings.Builder {
        private DimensionType dimensionType;
        private GeneratorType generatorType;
        private GameMode gameMode;
        private SerializationBehavior serializationBehavior;
        private boolean isEnabled, loadOnStartup, doesKeepSpawnLoaded, generateSpawnOnLoad, usesMapFeatures, isHardcore, commandsAllowed,
                doesGenerateBonusChest, isPvpEnabled;
        private long seed;

        public Builder() {
            reset();
        }

        @Override
        public WorldCreationSettings.Builder enabled(boolean state) {
            this.isEnabled = state;
            return this;
        }

        @Override
        public WorldCreationSettings.Builder loadsOnStartup(boolean state) {
            this.loadOnStartup = state;
            return this;
        }

        @Override
        public WorldCreationSettings.Builder keepsSpawnLoaded(boolean state) {
            this.doesKeepSpawnLoaded = state;
            return this;
        }

        @Override
        public WorldCreationSettings.Builder generateSpawnOnLoad(boolean state) {
            this.generateSpawnOnLoad = state;
            return this;
        }

        @Override
        public WorldCreationSettings.Builder seed(long seed) {
            this.seed = seed;
            return this;
        }

        @Override
        public WorldCreationSettings.Builder gameMode(GameMode gameMode) {
            this.gameMode = gameMode;
            return this;
        }

        @Override
        public WorldCreationSettings.Builder generator(GeneratorType type) {
            this.generatorType = type;
            return this;
        }

        @Override public WorldCreationSettings.Builder generatorModifiers(WorldGeneratorModifier... modifier) {
            return this;
        }

        @Override
        public WorldCreationSettings.Builder dimension(DimensionType type) {
            this.dimensionType = type;
            return this;
        }

        @Override
        public WorldCreationSettings.Builder usesMapFeatures(boolean state) {
            this.usesMapFeatures = state;
            return this;
        }

        @Override
        public WorldCreationSettings.Builder hardcore(boolean state) {
            this.isHardcore = state;
            return this;
        }

        @Override public WorldCreationSettings.Builder generatorSettings(DataContainer settings) {
            return this;
        }

        @Override
        public WorldCreationSettings.Builder pvp(boolean state) {
            this.isPvpEnabled = state;
            return this;
        }

        @Override
        public WorldCreationSettings.Builder commandsAllowed(boolean state) {
            this.commandsAllowed = state;
            return this;
        }

        @Override
        public WorldCreationSettings.Builder generateBonusChest(boolean state) {
            this.doesGenerateBonusChest = state;
            return this;
        }

        @Override
        public WorldCreationSettings.Builder serializationBehavior(SerializationBehavior behavior) {
            this.serializationBehavior = behavior;
            return this;
        }

        @Override
        public WorldCreationSettings.Builder from(WorldCreationSettings value) {
            this.dimensionType = value.getDimensionType();
            this.generatorType = value.getGeneratorType();
            this.gameMode = value.getGameMode();
            this.serializationBehavior = value.getSerializationBehavior();
            this.isEnabled = value.isEnabled();
            this.loadOnStartup = value.loadOnStartup();
            this.doesKeepSpawnLoaded = value.doesKeepSpawnLoaded();
            this.generateSpawnOnLoad = value.doesGenerateSpawnOnLoad();
            this.usesMapFeatures = value.usesMapFeatures();
            this.isHardcore = value.isHardcore();
            this.commandsAllowed = value.areCommandsAllowed();
            this.doesGenerateBonusChest = value.doesGenerateBonusChest();
            this.isPvpEnabled = value.isPVPEnabled();
            return this;
        }

        @Override
        public WorldCreationSettings.Builder from(WorldProperties properties) {
            this.dimensionType = properties.getDimensionType();
            this.generatorType = properties.getGeneratorType();
            this.gameMode = properties.getGameMode();
            this.serializationBehavior = properties.getSerializationBehavior();
            this.isEnabled = properties.isEnabled();
            this.loadOnStartup = properties.loadOnStartup();
            this.doesKeepSpawnLoaded = properties.doesKeepSpawnLoaded();
            this.generateSpawnOnLoad = properties.doesGenerateSpawnOnLoad();
            this.usesMapFeatures = properties.usesMapFeatures();
            this.isHardcore = properties.isHardcore();
            this.commandsAllowed = properties.areCommandsAllowed();
            this.doesGenerateBonusChest = false;
            this.isPvpEnabled = properties.isPVPEnabled();
            return this;
        }

        @Override
        public WorldCreationSettings build(String id, String name) {
            checkNotNull(id, "Id cannot be null!");
            checkNotNull(name, "Name cannot be null!");

            final ServerWorldCreationSettings settings = new ServerWorldCreationSettings();
            settings.id = id;
            settings.name = name;
            settings.dimensionType = this.dimensionType;
            settings.generatorType = this.generatorType;
            settings.gameMode = this.gameMode;
            settings.serializationBehavior = this.serializationBehavior;
            settings.isEnabled = this.isEnabled;
            settings.loadOnStartup = this.loadOnStartup;
            settings.doesKeepSpawnLoaded = this.doesKeepSpawnLoaded;
            settings.generateSpawnOnLoad = this.generateSpawnOnLoad;
            settings.usesMapFeatures = this.usesMapFeatures;
            settings.isHardcore = this.isHardcore;
            settings.commandsAllowed = this.commandsAllowed;
            settings.doesGenerateBonusChest = this.doesGenerateBonusChest;
            settings.isPvpEnabled = this.isPvpEnabled;
            settings.seed = this.seed;

            // TODO Put in GameRegistry as CatalogType
            return settings;
        }

        @Override
        public WorldCreationSettings.Builder reset() {
            this.dimensionType = DimensionTypes.OVERWORLD;
            this.generatorType = GeneratorTypes.DEFAULT;
            this.gameMode = GameModes.SURVIVAL;
            this.serializationBehavior = SerializationBehaviors.AUTOMATIC;
            this.isEnabled = true;
            this.loadOnStartup = true;
            this.doesKeepSpawnLoaded = true;
            this.generateSpawnOnLoad = true;
            this.usesMapFeatures = true;
            this.isHardcore = false;
            this.commandsAllowed = true;
            this.doesGenerateBonusChest = false;
            this.isPvpEnabled = true;
            this.seed = SpongeGame.instance.getConfiguration().getSeed();
            return this;
        }
    }
}
