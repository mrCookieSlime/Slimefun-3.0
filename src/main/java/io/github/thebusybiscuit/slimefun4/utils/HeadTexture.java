package io.github.thebusybiscuit.slimefun4.utils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.bakedlibs.dough.skins.PlayerSkin;

/**
 * This enum holds all currently used Head textures in Slimefun.
 * Credit for most of these goes to our main head designer "AquaLazuryt".
 * 
 * @author TheBusyBiscuit
 *
 */
public enum HeadTexture {

    PORTABLE_CRAFTER("72ec4a4bd8a58f8361f8a0303e2199d33d624ea5f92f7cb3414fee95e2d861"),
    ENDER_BACKPACK("2a3b34862b9afb63cf8d5779966d3fba70af82b04e83f3eaf6449aeba"),
    TIN_CAN("94da97f080e395b842c4cc82a840823d4dbd8ca688a206853e5783e4bfdc012"),
    MAGNET("aba8ebc4c6a81730947499bf7e1d5e73fed6c1bb2c051e96d35eb16d24610e7"),
    BACKPACK("40cb1e67b512ab2d4bf3d7ace0eaaf61c32cd4681ddc3987ceb326706a33fa"),
    COOLER("d4c1572584eb5de229de9f5a4f779d0aacbaffd33bcb33eb4536a6a2bc6a1"),
    RESTORED_BACKPACK("9c3681bf8a2738232fb305597f7e2a34a3a5c1356705249e9a365b0bcd04705a"),
    CHRISTMAS_PRESENT("6cef9aa14e884773eac134a4ee8972063f466de678363cf7b1a21a85b7"),
    EASTER_EGG("b2cd5df9d7f1fa8341fcce2f3c118e2f517e4d2d99df2c51d61d93ed7f83e13"),
    BATTERY("6e2dda6ef6185d4dd6ea8684e97d39ba8ab037e25f75cdea6bd29df8eb34ee"),
    CARBON("8b3a095b6b81e6b9853a19324eedf0bb9349417258dd173b8eff87a087aa"),
    COMPRESSED_CARBON("321d495165748d3116f99d6b5bd5d42eb8ba592bcdfad37fd95f9b6c04a3b"),
    POWER_CRYSTAL("53c1b036b6e03517b285a811bd85e73f5abfdacc1ddf90dff962e180934e3"),
    STONE_CHUNK("ce8f5adb14d6c9f6b810d027543f1a8c1f417e2fed993c97bcd89c74f5e2e8"),
    LAVA_CRYSTAL("a3ad8ee849edf04ed9a26ca3341f6033bd76dcc4231ed1ea63b7565751b27ac"),
    CHEESE("34febbc15d1d4cc62bedc5d7a2b6f0f46cd5b0696a884de75e289e35cbb53a0"),
    BUTTER("b66b19f7d635d03473891df33017c549363209a8f6328a8542c213d08525e"),
    DUCT_TAPE("b2faaceab6384fff5ed24bb44a4af2f584eb1382729ecd93a5369acfd6654"),
    URANIUM("c8b29afa6d6dc923e2e1324bf8192750f7bdbddc689632a2b6c18d9fe7a5e"),
    HEATING_COIL("7e3bc4893ba41a3f73ee28174cdf4fef6b145e41fe6c82cb7be8d8e9771a5"),
    COOLING_UNIT("754bad86c99df780c889a1098f77648ead7385cc1ddb093da5a7d8c4c2ae54d"),
    MOTOR("8cbca012f67e54de9aee72ff424e056c2ae58de5eacc949ab2bcd9683cec"),
    SAPPHIRE("e35032f4d7d01de8ec99d89f8723012d4e74fa73022c4facf1b57c7ff6ff0"),
    CARBONADO("12f4b1577f5160c6893172571c4a71d8b321cdceaa032c6e0e3b60e0b328fa"),
    RAW_CARBONADO("eb49e6ec10771e899225aea73cd8cf03684f411d1415c7323c93cb9476230"),
    NEPTUNIUM("4edea6bfd37e49de43f154fe6fca617d4129e61b95759a3d49a15935a1c2dcf0"),
    PLUTONIUM("25cf91b7388665a6d7c1b6026bdb2322c6d278997a44478677cbcc15f76124f"),
    BOOSTED_URANIUM("6837ca12f222f4787196a17b8ab656985f8404c50767adbcb6e7f14254fee"),
    GENERATOR("9343ce58da54c79924a2c9331cfc417fe8ccbbea9be45a7ac85860a6c730"),
    CAPACITOR_25("91361e576b493cbfdfae328661cedd1add55fab4e5eb418b92cebf6275f8bb4"),
    CAPACITOR_50("305323394a7d91bfb33df06d92b63cb414ef80f054d04734ea015a23c539"),
    CAPACITOR_75("5584432af6f382167120258d1eee8c87c6e75d9e479e7b0d4c7b6ad48cfeef"),
    CAPACITOR_100("7a2569415c14e31c98ec993a2f99e6d64846db367a13b199965ad99c438c86c"),
    PROGRAMMABLE_ANDROID("3503cb7ed845e7a507f569afc647c47ac483771465c9a679a54594c76afba"),
    PROGRAMMABLE_ANDROID_FARMER("f9d33357e8418823bf783de92de80291b4ebd392aec8706698e06896d498f6"),
    PROGRAMMABLE_ANDROID_MINER("e638a28541ab3ae0a723d5578738e08758388ec4c33247bd4ca13482aef334"),
    PROGRAMMABLE_ANDROID_WOODCUTTER("d32a814510142205169a1ad32f0a745f18e9cb6c66ee64eca2e65babdef9ff"),
    PROGRAMMABLE_ANDROID_BUTCHER("3b472df0ad9a3be88f2e5d5d422d02b116d64d8df1475ed32e546afc84b31"),
    PROGRAMMABLE_ANDROID_FISHERMAN("345e8733a73114333b98b3601751241722f4713e1a1a5d36fbb132493f1c7"),
    GPS_TRANSMITTER("b0c9c1a022f40b73f14b4cba37c718c6a533f3a2864b6536d5f456934cc1f"),
    GPS_CONTROL_PANEL("ddcfba58faf1f64847884111822b64afa21d7fc62d4481f14f3f3bcb6330"),
    GEO_SCANNER("2ad8cfeb387a56e3e5bcf85345d6a417b242293887db3ce3ba91fa409b254b86"),
    GEO_MINER("a37741f764dd3dd7adaeb43b63d3959eb70e5eb28f15d6b34cab34a1d1f60387"),
    OIL_PUMP("afe1a040a425e31a46d4f9a9b9806fa2f0c47ee84711cc1932fd8ab32b2d038"),
    OIL_BUCKET("6ce04b41d19ec7927f982a63a94a3d79f78ecec33363051fde0831bfabdbd"),
    FUEL_BUCKET("a84ddca766725b8b97413f259c3f7668070f6ae55483a90c8e5525394f9c099"),
    ELECTRIC_PRESS("8d5cf92bc79ec19f4106441affff1406a1367010dcafb197dd94cfca1a6de0fc"),
    ENERGY_REGULATOR("d78f2b7e5e75639ea7fb796c35d364c4df28b4243e66b76277aadcd6261337"),
    ENERGY_CONNECTOR("1085e098756b995b00241644089c55a8f9acde35b9a37785d5e057a923613b"),
    NETHER_ICE("3ce2dad9baf7eaba7e80d4d0f9fac0aab01a76b12fb71c3d2af2a16fdd4c7383"),
    ENRICHED_NETHER_ICE("7c818aa13aabc7294838d21caac057e97bd8c89641a0c0f8a55442ff4e27"),
    NETHER_ICE_COOLANT_CELL("8d3cd412555f897016213e5d6c7431b448b9e5644e1b19ec51b5316f35840e0"),
    CARGO_MANAGER("e510bc85362a130a6ff9d91ff11d6fa46d7d1912a3431f751558ef3c4d9c2"),
    CARGO_CONNECTOR_NODE("07b7ef6fd7864865c31c1dc87bed24ab5973579f5c6638fecb8dedeb443ff0"),
    CARGO_INPUT_NODE("16d1c1a69a3de9fec962a77bf3b2e376dd25c873a3d8f14f1dd345dae4c4"),
    CARGO_OUTPUT_NODE("55b21fd480c1c43bf3b9f842c869bdc3bc5acc2599bf2eb6b8a1c95dce978f"),
    FILLED_CAN("b439e3f5acbee9be4c4259289d6d9f35c635ffa661114687b3ea6dda8c79"),
    EXP_COLLECTOR("1762a15b04692a2e4b3fb3663bd4b78434dce1732b8eb1c7a9f7c0fbf6f"),
    COOLANT_CELL("de4073be40cb3deb310a0be959b4cac68e825372728fafb6c2973e4e7c33"),
    TRASH_CAN("32d41042ce99147cc38cac9e46741576e7ee791283e6fac8d3292cae2935f1f"),
    SCRIPT_START("4ae29422db4047efdb9bac2cdae5a0719eb772fccc88a66d912320b343c341"),
    SCRIPT_REPEAT("bc8def67a12622ead1decd3d89364257b531896d87e469813131ca235b5c7"),
    SCRIPT_WAIT("2ee174f41e594e64ea3141c07daf7acf1fa045c230b2b0b0fb3da163db22f455"),
    SCRIPT_FORWARD("d9bf6db4aeda9d8822b9f736538e8c18b9a4844f84eb45504adfbfee87eb"),
    SCRIPT_UP("105a2cab8b68ea57e3af992a36e47c8ff9aa87cc8776281966f8c3cf31a38"),
    SCRIPT_DOWN("c01586e39f6ffa63b4fb301b65ca7da8a92f7353aaab89d3886579125dfbaf9"),
    SCRIPT_LEFT("a185c97dbb8353de652698d24b64327b793a3f32a98be67b719fbedab35e"),
    SCRIPT_RIGHT("31c0ededd7115fc1b23d51ce966358b27195daf26ebb6e45a66c34c69c34091"),
    SCRIPT_DIG_UP("2e6ce011ac9a7a75b2fcd408ad21a3ac1722f6e2eed8781cafd12552282b88"),
    SCRIPT_DIG_FORWARD("b6ea2135838461534372f2da6c862d21cd5f3d2c7119f2bb674bbd42791"),
    SCRIPT_DIG_DOWN("8d862024108c785bc0ef7199ec77c402dbbfcc624e9f41f83d8aed8b39fd13"),
    SCRIPT_ATTACK("c7e6c40f68b775f2efcd7bd9916b327869dcf27e24c855d0a18e07ac04fe1"),
    SCRIPT_CHOP_TREE("64ba49384dba7b7acdb4f70e9361e6d57cbbcbf720cf4f16c2bb83e4557"),
    SCRIPT_FISH("fd4fde511f4454101e4a2a72bc86f12985dfcda76b64bb24dc63a9fa9e3a3"),
    SCRIPT_FARM_FORWARD("4de9a522c3d9e7d85f3d82c375dc37fecc856dbd801eb3bcedc1165198bf"),
    SCRIPT_FARM_DOWN("2d4296b333d25319af3f33051797f9e6d821cd19a014fb7137beb86a4e9e96"),
    SCRIPT_PUSH_ITEMS("90a4dbf6625c42be57a8ba2c330954a76bdf22785540e87a5c9672685238ec"),
    SCRIPT_PULL_FUEL("2432f5282a50745b912be14deda581bd4a09b977a3c32d7e9578491fee8fa7"),
    SCRIPT_NEW("171d8979c1878a05987a7faf21b56d1b744f9d068c74cffcde1ea1edad5852"),
    SCRIPT_PAUSE("16139fd1c5654e56e9e4e2c8be7eb2bd5b499d633616663feee99b74352ad64"),
    GLOBE_OVERWORLD("c9c8881e42915a9d29bb61a16fb26d059913204d265df5b439b3d792acd56"),
    GLOBE_NETHER("d83571ff589f1a59bb02b80800fc736116e27c3dcf9efebede8cf1fdde"),
    GLOBE_THE_END("c6cac59b2aae489aa0687b5d802b2555eb14a40bd62b21eb116fa569cdb756"),
    DEATHPOINT("1ae3855f952cd4a03c148a946e3f812a5955ad35cbcb52627ea4acd47d3081"),
    NUCLEAR_REACTOR("fa5de0bc2bfb5cc2d23eb72f96402ada479524dd0de404bc23b6dacee3ffd080"),
    NETHER_STAR_REACTOR("a11ed1d1b25b624665ecdddc3d3a5dff0b9f35e3de77a12f516e60fe8501cc8d"),
    UNKNOWN("46ba63344f49dd1c4f5488e926bf3d9e2b29916a6c50d610bb40a5273dc8c82"),
    MISSING_TEXTURE("e9eb9da26cf2d3341397a7f4913ba3d37d1ad10eae30ab25fa39ceb84bc"),
    MINECRAFT_CHUNK("8449b9318e33158e64a46ab0de121c3d40000e3332c1574932b3c849d8fa0dc2"),
    CHEST_TERMINAL("7a44ff3a5f49c69cab676bad8d98a063fa78cfa61916fdef3e267557fec18283"),
    CARGO_ARROW_LEFT("f2599bd986659b8ce2c4988525c94e19ddd39fad08a38284a197f1b70675acc"),
    CARGO_ARROW_RIGHT("c2f910c47da042e4aa28af6cc81cf48ac6caf37dab35f88db993accb9dfe516"),
    ADD_NEW_LANGUAGE("3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716"),
    IRON_GOLEM("89091d79ea0f59ef7ef94d7bba6e5f17f2f7d4572c44f90f76c4819a714"),
    PIGLIN_HEAD("2882af1294a74023e6919a31d1a027310f2e142afb4667d230d155e7f21dbb41"),
    NECROTIC_SKULL("7953b6c68448e7e6b6bf8fb273d7203acd8e1be19e81481ead51f45de59a8"),
    VANILLA_AUTO_CRAFTER("80a4334f6a61e40c0c63deb665fa7b581e6eb259f7a3207ced7a1ff8bdc8a9f9"),
    ENHANCED_AUTO_CRAFTER("5038298306a5e28584df39e88896917c38d40a326226d8c83070723c95798b24"),
    ARMOR_AUTO_CRAFTER("5cbd9f5ec1ed007259996491e69ff649a3106cf920227b1bb3a71ee7a89863f"),
    EXCLAMATION_MARK("2e3f50ba62cbda3ecf5479b62fedebd61d76589771cc19286bf2745cd71e47c6"),
    CARGO_MOTOR("8e47f99abcd645a3ef1122c9d850a981979f431ba293255c1680e91ab117ed35"),
    CRAFTING_MOTOR("1003620899f1afa271e8e521ecbee2977a06c8529d3f389e8cc04af06d8c7940");

    private final String texture;
    private final UUID uuid;

    HeadTexture(@Nonnull String texture) {
        Validate.notNull(texture, "Texture cannot be null");
        Validate.isTrue(CommonPatterns.HEXADECIMAL.matcher(texture).matches(), "Textures must be in hexadecimal.");

        this.texture = texture;
        this.uuid = UUID.nameUUIDFromBytes(texture.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * This returns the texture hash for this particular head.
     * 
     * @return The associated texture hash
     */
    public @Nonnull String getTexture() {
        return texture;
    }

    /**
     * This returns the {@link UUID} for this {@link HeadTexture}.
     * The {@link UUID} is generated from the texture and cached for
     * performance reasons.
     * 
     * @return The {@link UUID} for this {@link HeadTexture}
     */
    public @Nonnull UUID getUniqueId() {
        return uuid;
    }

    /**
     * This method returns an {@link ItemStack} with the given texture assigned to it.
     * 
     * @return A custom head {@link ItemStack}
     */
    public @Nonnull ItemStack getAsItemStack() {
        return SlimefunUtils.getCustomHead(getTexture());
    }

    public @Nonnull PlayerSkin getAsSkin() {
        return PlayerSkin.fromHashCode(texture);
    }

}
