/*
 * Hello Minecraft! Launcher
 * Copyright (C) 2020  huangyuhui <huanghongxun2008@126.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.tungsten.fclcore.download;

import static com.tungsten.fclcore.util.Pair.pair;

import com.tungsten.fclcore.download.fabric.FabricAPIVersionList;
import com.tungsten.fclcore.download.fabric.FabricVersionList;
import com.tungsten.fclcore.download.forge.ForgeBMCLVersionList;
import com.tungsten.fclcore.download.game.GameVersionList;
import com.tungsten.fclcore.download.liteloader.LiteLoaderBMCLVersionList;
import com.tungsten.fclcore.download.neoforge.NeoForgeBMCLVersionList;
import com.tungsten.fclcore.download.optifine.OptiFineBMCLVersionList;
import com.tungsten.fclcore.download.quilt.QuiltAPIVersionList;
import com.tungsten.fclcore.download.quilt.QuiltVersionList;
import com.tungsten.fclcore.util.Pair;
import com.tungsten.fclauncher.utils.FCLPath;

import java.util.Arrays;
import java.util.List;

public final class BMCLAPIDownloadProvider implements DownloadProvider {
    private final String apiRoot;
    private final GameVersionList game;
    private final FabricVersionList fabric;
    private final FabricAPIVersionList fabricApi;
    private final ForgeBMCLVersionList forge;
    private final NeoForgeBMCLVersionList neoforge;
    private final LiteLoaderBMCLVersionList liteLoader;
    private final OptiFineBMCLVersionList optifine;
    private final QuiltVersionList quilt;
    private final QuiltAPIVersionList quiltApi;
    private final List<Pair<String, String>> replacement;

    public BMCLAPIDownloadProvider(String apiRoot) {
        this.apiRoot = apiRoot;
        this.game = new GameVersionList(this);
        this.fabric = new FabricVersionList(this);
        this.fabricApi = new FabricAPIVersionList(this);
        this.forge = new ForgeBMCLVersionList(apiRoot);
        this.neoforge = new NeoForgeBMCLVersionList(apiRoot);
        this.liteLoader = new LiteLoaderBMCLVersionList(this);
        this.optifine = new OptiFineBMCLVersionList(apiRoot);
        this.quilt = new QuiltVersionList(this);
        this.quiltApi = new QuiltAPIVersionList(this);
        this.replacement = Arrays.asList(
                pair("https://bmclapi2.bangbang93.com", apiRoot),
                pair("https://launchermeta.mojang.com", apiRoot),
                pair("https://piston-meta.mojang.com", apiRoot),
                pair("https://piston-data.mojang.com", apiRoot),
                pair("https://launcher.mojang.com", apiRoot),
                pair("https://libraries.minecraft.net", apiRoot + "/libraries"),
                pair("http://files.minecraftforge.net/maven", apiRoot + "/maven"),
                pair("https://files.minecraftforge.net/maven", apiRoot + "/maven"),
                pair("https://maven.minecraftforge.net", apiRoot + "/maven"),
                pair("https://maven.neoforged.net/releases", apiRoot + "/maven"),
                pair("http://dl.liteloader.com/versions/versions.json", apiRoot + "/maven/com/mumfrey/liteloader/versions.json"),
                pair("http://dl.liteloader.com/versions", apiRoot + "/maven"),
                pair("https://meta.fabricmc.net", apiRoot + "/fabric-meta"),
                pair("https://maven.fabricmc.net", apiRoot + "/maven"),
                pair("https://authlib-injector.yushi.moe", apiRoot + "/mirrors/authlib-injector"),
                pair("https://repo1.maven.org/maven2", FCLPath.Prop.getProperty("maven2-download-url", "null://"))
        );
    }

    public String getApiRoot() {
        return apiRoot;
    }

    @Override
    public String getVersionListURL() {
        return apiRoot + "/mc/game/version_manifest.json";
    }

    @Override
    public String getAssetBaseURL() {
        return apiRoot + "/assets/";
    }

    @Override
    public VersionList<?> getVersionListById(String id) {
        switch (id) {
            case "game":
                return game;
            case "fabric":
                return fabric;
            case "fabric-api":
                return fabricApi;
            case "forge":
                return forge;
            case "neoforge":
                return neoforge;
            case "liteloader":
                return liteLoader;
            case "optifine":
                return optifine;
            case "quilt":
                return quilt;
            case "quilt-api":
                return quiltApi;
            default:
                throw new IllegalArgumentException("Unrecognized version list id: " + id);
        }
    }

    @Override
    public String injectURL(String baseURL) {
        for (Pair<String, String> pair : replacement) {
            if (baseURL.startsWith(pair.getKey())) {
                return pair.getValue() + baseURL.substring(pair.getKey().length());
            }
        }

        return baseURL;
    }

    @Override
    public int getConcurrency() {
        return Math.max(Runtime.getRuntime().availableProcessors() * 2, 6);
    }
}
