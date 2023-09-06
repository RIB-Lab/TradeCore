/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.general.NBTTagNames;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.item.mod.ModWeaponAttribute;
import net.riblab.tradecore.item.mod.ShortHandModNames;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * このクラスの新しいインスタンスを作ることでアイテムの実体を1から作成することができる<br>
 * 引数にアイテムの実体または固有アイテムの型を渡すことで改造することもできる<br>
 */
public final class ItemCreator {

    private ItemStack itemStack;
    
    private static final Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

    /**
     * バニラアイテムの型からバニラアイテムの実体/固有アイテムの型を生成する
     *
     * @param material バニラアイテムの型
     */
    @ParametersAreNonnullByDefault
    public ItemCreator(Material material) {
        this.itemStack = new ItemStack(material, 1, (short) 0);
    }

    /**
     * バニラアイテムの実体/固有アイテムの型/固有アイテムの実体を改造する
     *
     * @param itemStack 実体
     */
    @ParametersAreNonnullByDefault
    public ItemCreator(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * バニラアイテムの名前を変更する
     *
     * @param name 名前
     * @return このクラス自体
     */
    @ParametersAreNonnullByDefault
    public ItemCreator setName(Component name) {
        ItemMeta meta = itemStack.getItemMeta();
        if (Objects.nonNull(meta)) {
            meta.displayName(name);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * バニラアイテムの型を変更する
     *
     * @param type 設定したい型
     * @return このクラス自体
     */
    @ParametersAreNonnullByDefault
    public ItemCreator setType(Material type) {
        itemStack.setType(type);
        return this;
    }

    /**
     * このクラスが改造しているバニラアイテムの型を取得する
     *
     * @return バニラアイテムの型
     */
    @Nonnull
    public Material getType() {
        return itemStack.getType();
    }

    /**
     * アイテムの量を設定する
     *
     * @param amount 設定した医療
     * @return このクラス自体
     */
    public ItemCreator setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    /**
     * このクラスが改造しているアイテムの量を取得する
     *
     * @return アイテムの量
     */
    public int getAmount() {
        return itemStack.getAmount();
    }

    /**
     * アイテムにブロックデータを設定する
     *
     * @param data 設定したいブロックデータ
     * @return このクラス自体
     */
    @ParametersAreNonnullByDefault
    public ItemCreator setData(BlockData data) {
        if (itemStack instanceof BlockDataMeta blockData) {
            blockData.setBlockData(data);
        }
        return this;
    }

    /**
     * アイテムが破壊不可能かどうか設定する
     *
     * @param value 破壊不可能かどうか
     * @return このクラス自体
     */
    public ItemCreator setUnbreakable(boolean value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (Objects.nonNull(meta)) {
            meta.setUnbreakable(value);
            itemStack.setItemMeta(meta);
            itemStack.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        return this;
    }

    /**
     * アイテムのフラグ(エンチャントを隠すかなど)を設定する
     *
     * @param flags 設定したいフラグ
     * @return このクラス自体
     */
    @ParametersAreNonnullByDefault
    public ItemCreator SetFlags(ItemFlag... flags) {
        ItemMeta meta = itemStack.getItemMeta();
        if (Objects.nonNull(meta)) {
            meta.addItemFlags(flags);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * アイテムの説明文を消去する
     *
     * @return このクラス自体
     */
    public ItemCreator clearlores() {
        setLores(new ArrayList<>());
        return this;
    }

    /**
     * 1行のアイテムの説明文を設定する
     *
     * @param lore 設定したい説明文
     * @return このクラス自体
     */
    @ParametersAreNullableByDefault
    public ItemCreator setLore(Component lore) {
        List<Component> lores = new ArrayList<>();
        lores.add(lore);
        return setLores(lores);
    }

    /**
     * アイテムの説明文を設定する
     *
     * @param lores 設定したい説明文達
     * @return このクラス自体
     */
    @ParametersAreNullableByDefault
    public ItemCreator setLores(List<Component> lores) {
        ItemMeta meta = itemStack.getItemMeta();
        if (Objects.nonNull(meta)) {
            meta.lore(lores);
        }
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * アイテムの説明文を1行追加する
     *
     * @param lore 追加したい説明文
     * @return このクラス自体
     */
    @ParametersAreNullableByDefault
    public ItemCreator addLore(Component lore) {
        if (Objects.isNull(lore)) {
            return this;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (Objects.nonNull(meta)) {
            List<Component> lores = meta.lore();
            if (Objects.isNull(lores)) {
                lores = new ArrayList<>();
            }
            lores.add(lore);
            meta.lore(lores);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * アイテムの説明文を複数行追加する
     */
    @ParametersAreNonnullByDefault
    public ItemCreator addLores(List<Component> newLore) {
        if (newLore.size() == 0) {
            return this;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (Objects.nonNull(meta)) {
            List<Component> lores = meta.lore();
            if (Objects.isNull(lores)) {
                lores = new ArrayList<>();
            }
            lores.addAll(newLore);
            meta.lore(lores);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * アイテムの全ての説明文を取得
     *
     * @return アイテムの説明文
     */
    @Nullable
    public List<Component> getLores() {
        return itemStack.getItemMeta().lore();
    }

    /**
     * アイテムに1レベルのエンチャントを追加する
     */
    @ParametersAreNonnullByDefault
    public ItemCreator addEnchantment(Enchantment enchantment) {
        return addEnchantment(enchantment, 1);
    }

    /**
     * アイテムにエンチャントを付与する
     *
     * @param enchantment 付与したいエンチャント
     * @param level       エンチャントのレベル
     * @return このクラス自体
     */
    @ParametersAreNonnullByDefault
    public ItemCreator addEnchantment(Enchantment enchantment, int level) {
        itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    /**
     * ビルダーを終了して作成または改造したアイテムを受け取る
     *
     * @return 作成または改造されたアイテム
     */
    @Nonnull
    public ItemStack create() {
        return this.itemStack.clone();
    }

    /**
     * アイテムの名前を取得する
     *
     * @return アイテムの名前
     */
    @Nonnull
    public String getName() {
        return itemStack.getItemMeta().getDisplayName();
    }

    /**
     * アイテムのエンチャントを隠す
     *
     * @return このクラス自体
     */
    public ItemCreator hideEnchantment() {
        itemStack.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    /**
     * アイテムの属性を隠す
     *
     * @return このクラス自体
     */
    public ItemCreator hideAttributes() {
        itemStack.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        return this;
    }

    /**
     * アイテムの耐久値を減らす/回復させる
     *
     * @param damage 減らしたい/回復させたい耐久値の量
     * @return このクラス自体
     */
    public ItemCreator damage(int damage) {
        if (!(itemStack.getItemMeta() instanceof Damageable damageable)) {
            return this;
        }

        damageable.setDamage(damageable.getDamage() + damage);
        itemStack.setItemMeta(damageable);
        return this;
    }

    /**
     * アイテムにint型のNBTを追加する
     */
    @ParametersAreNonnullByDefault
    public ItemCreator setIntNBT(String key, int value) {
        NBTItem item = new NBTItem(itemStack);
        item.setInteger(key, value);
        itemStack = item.getItem();
        return this;
    }

    /**
     * アイテムからint型のNBTを取得する
     * @param key
     * @return
     */
    @Nonnull
    public Integer getIntNBT(String key) {
        return new NBTItem(itemStack).getInteger(key);
    }

    /**
     * アイテムにstr型のNBTを追加する
     */
    @ParametersAreNonnullByDefault
    public ItemCreator setStrNBT(String key, String value) {
        NBTItem item = new NBTItem(itemStack);
        item.setString(key, value);
        itemStack = item.getItem();
        return this;
    }

    @Nullable
    public String getStrNBT(String key) {
        return new NBTItem(itemStack).getString(key);
    }

    /**
     * アイテムにBoolean型のNBTを追加する
     */
    @ParametersAreNonnullByDefault
    public ItemCreator setBooleanNBT(String key, boolean value) {
        NBTItem item = new NBTItem(itemStack);
        item.setBoolean(key, value);
        itemStack = item.getItem();
        return this;
    }

    /**
     * アイテムからBoolean型のNBTを取得する
     */
    @Nullable
    public Boolean getBooleanNBT(String key) {
        return new NBTItem(itemStack).getBoolean(key);
    }

    /**
     * アイテムにランダムなIDを割り振る
     */
    @ParametersAreNonnullByDefault
    public ItemCreator assignRandomIDtoItem(String key) {
        int id = new Random().nextInt(Integer.MAX_VALUE);
        setIntNBT(key, id);
        return this;
    }

    /**
     * アイテムにカスタムモデルデータを設定する
     */
    public ItemCreator setCustomModelData(int id) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setCustomModelData(id);
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * アイテムのカスタムモデルデータの番号を取得
     */
    public int getCustomModelData() {
        ItemMeta meta = itemStack.getItemMeta();
        return meta.getCustomModelData();
    }

    /**
     * アイテムにバニラの攻撃速度のパラメータを設定する(-4以上)
     */
    public ItemCreator setAttackSpeedAttr(double value) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier("generic.attackSpeed", value, AttributeModifier.Operation.ADD_NUMBER));
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * アイテムにレアリティなどのアイテムそれぞれで異なるべき値を書きこむ
     */
    public ItemCreator writeItemRandomMod(IItemMod<?> mod){
        NBTItem item = new NBTItem(itemStack);
        //ItemModの数値をJsonとして焼きこむ
        String json = gson.toJson(mod.getParam());
        String key = ShortHandModNames.getShortHandNameFromClass((Class<? extends IItemMod<?>>) mod.getClass());
        item.getOrCreateCompound(NBTTagNames.ITEMMOD.get()).setString(key, json);
        itemStack = item.getItem();
        return this;
    }

    /**
     * アイテムが持つこのプラグイン特有のmodをアイテムにNBTとして書きこむ
     */
    public ItemCreator writeItemRandomMods(List<IItemMod<?>> mods){
        mods.forEach(this::writeItemRandomMod);
        return this;
    }

    /**
     * アイテムそれぞれに付与されたレアリティなどのランダム化されたパラメータを取得する
     */
    public List<IItemMod<?>> getItemRandomMods(){
        List<IItemMod<?>> modList = new ArrayList<>();
        NBTItem item = new NBTItem(itemStack);
        NBTCompound compound = item.getOrCreateCompound(NBTTagNames.ITEMMOD.get());
        for (String key : compound.getKeys()) {
            IItemMod<?> mod = null;
            try {
                Class<? extends IItemMod<?>> clazz = ShortHandModNames.getClassFromShortHandName(key);
                if(Objects.isNull(clazz))
                    continue;

                //Jsonを元の型に還元する
                Constructor<?> constructor = clazz.getConstructors()[0];
                Type[] parameterTypes = constructor.getGenericParameterTypes();
                String json = compound.getString(key);
                Object arg =  gson.fromJson(json, parameterTypes[0]);
                mod = (IItemMod<?>) constructor.newInstance(arg);

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                //クラスの名前が変わっただけかもしれないし、modがなかった時のフォールバックも用意してあるので、握りつぶす
            }
            
            if(Objects.nonNull(mod) && Objects.nonNull(mod.getParam()))
                modList.add(mod);
        }
        return modList;
    }

    /**
     * アイテムの攻撃速度modからバニラの攻撃速度を抽出して、アイテムに書きこむ
     */
    public ItemCreator setAttackSpeedAttr(@Nullable ModWeaponAttribute weaponMod){
        if(Objects.nonNull(weaponMod))
            return setAttackSpeedAttr(weaponMod.getParam().getAttackSpeed());
        return this;
    }
}
