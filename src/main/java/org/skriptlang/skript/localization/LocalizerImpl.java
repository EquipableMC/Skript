package org.skriptlang.skript.localization;

import ch.njol.skript.SkriptAPIException;
import ch.njol.skript.localization.Language;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.addon.SkriptAddon;

final class LocalizerImpl implements Localizer {

	private final SkriptAddon addon;

	LocalizerImpl(SkriptAddon addon) {
		this.addon = addon;
	}

	private Class<?> source;
	private String languageFileDirectory;
	private String dataFileDirectory;

	@Override
	public void setSourceDirectories(Class<?> source, String languageFileDirectory, @Nullable String dataFileDirectory) {
		if (this.source != null) {
			throw new SkriptAPIException("A localizer's source directories may only be set once.");
		}
		this.source = source;
		this.languageFileDirectory = languageFileDirectory;
		this.dataFileDirectory = dataFileDirectory;
		Language.loadDefault(addon);
	}

	@Override
	public @Nullable Class<?> source() {
		return source;
	}

	@Override
	public @Nullable String languageFileDirectory() {
		return languageFileDirectory;
	}

	@Override
	public @Nullable String dataFileDirectory() {
		return dataFileDirectory;
	}

	@Override
	public @Nullable String translate(String key) {
		return Language.get_(key);
	}

	static final class UnmodifiableLocalizer implements Localizer {

		private final Localizer localizer;

		UnmodifiableLocalizer(Localizer localizer) {
			this.localizer = localizer;
		}

		@Override
		public void setSourceDirectories(Class<?> source, String languageFileDirectory, @Nullable String dataFileDirectory) {
			throw new UnsupportedOperationException("An unmodifiable localizer cannot have translations added.");
		}

		@Override
		public @Nullable Class<?> source() {
			return localizer.source();
		}

		@Override
		public @Nullable String languageFileDirectory() {
			return localizer.languageFileDirectory();
		}

		@Override
		public @Nullable String dataFileDirectory() {
			return localizer.dataFileDirectory();
		}

		@Override
		public @Nullable String translate(String key) {
			return localizer.translate(key);
		}

	}

}