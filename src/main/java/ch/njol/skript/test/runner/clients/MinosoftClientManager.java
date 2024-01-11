/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.test.runner.clients;

import ch.njol.skript.Skript;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.jdt.annotation.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class MinosoftClientManager implements ClientManager {

	private Path minsoftJarPath;
	private Set<Process> minsoftProcesses = new HashSet<>();

	public MinosoftClientManager() {
		this.minsoftJarPath = findMinsoftJar();
	}

	@Nullable
	private Path findMinsoftJar() {
		Path skriptDataFolder = Skript.getInstance().getDataFolder().toPath();
		if (SystemUtils.IS_OS_WINDOWS) {
			return skriptDataFolder.resolve("minosoft").resolve("minosoft-windows.jar").toAbsolutePath();
		} else if (SystemUtils.IS_OS_LINUX) {
			return skriptDataFolder.resolve("minosoft").resolve("minosoft-linux.jar").toAbsolutePath();
		}
		return null; // todo: support macs
	}

	@Override
	public boolean isAvailable() {
		return minsoftJarPath != null && Files.exists(minsoftJarPath);
	}

	@Override
	public void launchClient(String username, String serverAddress, String serverPort) throws ClientLaunchException {
		Path javaPath = Paths.get(System.getProperty("java.home"), "bin", "java").toAbsolutePath().normalize();
		ProcessBuilder minosoftProcessBuilder = new ProcessBuilder(javaPath.toString(), "-jar", minsoftJarPath.toString(), "--headless");
		try {
			Process minsoftProcess = minosoftProcessBuilder.start();
			BufferedWriter minsoftCommandWriter = new BufferedWriter(new OutputStreamWriter(minsoftProcess.getOutputStream()));
			minsoftCommandWriter.write("account add offline " + username + '\n');
			minsoftCommandWriter.write("account select " + username + '\n');
			minsoftCommandWriter.write("connect " + serverAddress + ":" + serverPort + '\n');
			minsoftCommandWriter.close();
			minsoftProcesses.add(minsoftProcess);
		} catch (IOException exception) {
			throw new ClientLaunchException(exception);
		}
	}

	@Override
	public int getClientCount() {
		return minsoftProcesses.size();
	}

	@Override
	public void killClients() {
		minsoftProcesses.forEach(Process::destroyForcibly);
	}

}