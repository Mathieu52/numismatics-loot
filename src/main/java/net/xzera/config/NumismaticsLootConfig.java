package net.xzera.config;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import net.xzera.ModifierConfig;
import net.xzera.NumismaticsLoot;
import net.xzera.config.Exception.DeserializationException;

import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NumismaticsLootConfig {
    private final ArrayList<ModifierConfig> modifiers;

    private final RewardConfig rewardConfig = new RewardConfig(RewardMode.DEFAULT_REWARD);
    private final DropRateConfig dropRateConfig = new DropRateConfig();
    private final LootingConfig lootingConfig = new LootingConfig();
	private final GrantBookConfig grantBookConfig = new GrantBookConfig();
    private final PlayerKillConfig playerKillConfig = new PlayerKillConfig();


	private final Logger logger;
	private NumismaticsLootConfig(Logger logger) {
		modifiers = new ArrayList<>();
		this.logger = logger;
	}

    public ArrayList<ModifierConfig> getModifiers() {
        return modifiers;
    }

    public RewardConfig getRewardConfig() {
        return rewardConfig;
    }

    public DropRateConfig getDropRateConfig() {
        return dropRateConfig;
    }

    public LootingConfig getLootingConfig() {
        return lootingConfig;
    }

	public GrantBookConfig getGrantBookConfig() {
		return grantBookConfig;
	}

    public PlayerKillConfig getPlayerKillConfig() {
        return playerKillConfig;
    }


	public static NumismaticsLootConfig load(Logger logger, Path path) {
		NumismaticsLoot.LOGGER.info("Loading configuration...");
        NumismaticsLootConfig config = new NumismaticsLootConfig(logger);
        config.reload(path);

        return config;
    }

    public void reload(Path path) {
        File file = path.toFile();
		logger.info(path.toString());
        try {
            if (file.exists()) {
				Toml tomlReader = new Toml().read(file);
                this.rewardConfig.deserialize(tomlReader);
                this.dropRateConfig.deserialize(tomlReader);
                this.lootingConfig.deserialize(tomlReader);
				this.grantBookConfig.deserialize(tomlReader);
                this.playerKillConfig.deserialize(tomlReader);

                modifiers.clear();
                List<Toml> modifiersToml = tomlReader.getTables("modifiers");
				if (modifiersToml != null) {
					for (Toml toml : modifiersToml) {
						ModifierConfig modifierConfig = new ModifierConfig(null);
						modifierConfig.deserialize(toml);
						this.modifiers.add(modifierConfig);
					}
				}
            } else {
                this.save(path);
            }
        } catch (DeserializationException e) {
			logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean save(Path path) {
		try {
			TomlWriter tomlWriter = new TomlWriter.Builder()
					.indentValuesBy(4)
					.build();

			Map<String, Object> map = new HashMap<>();

			List<Map<String, Object>> list = new ArrayList<>();

			for (ModifierConfig config : modifiers) {
				list.add(config.serialize(true));
			}

			map.putAll(rewardConfig.serialize());
			map.putAll(dropRateConfig.serialize());
			map.putAll(lootingConfig.serialize());
			map.putAll(playerKillConfig.serialize());
			map.put("modifiers", list);

			File file = path.toFile();
			try {
				tomlWriter.write(map, file);
			} catch (IOException e) {
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
        return true;
    }
}
