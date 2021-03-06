package com.github.leeyazhou.scf.client.proxy.builder;

import java.lang.reflect.Type;

import com.github.leeyazhou.scf.core.entity.Out;

/**
 * Parameter
 */
public class Parameter {

  private Class<?> clazz;
  private Type type;
  private Object value;
  private ParaType paraType;
  private boolean isGeneric;
  private String simpleName;
  private Class<?> containerClass;
  private Class<?> itemClass;
  private Class<?>[] itemClass_;

  public Parameter(Object value, Class<?> clazz, Type type) throws ClassNotFoundException {
    this.setValue(value);
    this.setClazz(clazz);
    this.setType(type);
    this.setParaType(ParaType.In);
    init(value, clazz, type);
  }

  public Parameter(Object value, Class<?> clazz, Type type, ParaType ptype) throws ClassNotFoundException {
    this.setClazz(clazz);
    this.setType(type);
    this.setValue(value);
    this.setParaType(ptype);
    init(value, clazz, type);
  }

  /**
   * @param value null
   * @param clazz 方法返回类型(ex:interface java.util.Map)
   * @param type 方法 底层方法的正式返回类型的 Type 对象 (ex:java.util.Map<java.lang.String, java.lang.String>)
   * @throws ClassNotFoundException
   */
  private void init(Object value, Class<?> clazz, Type type) throws ClassNotFoundException {
    Class<?>[] itemClassO_ = new Class<?>[2];
    if (!clazz.equals(type) && !clazz.getCanonicalName().equalsIgnoreCase(type.toString())) {
      String itemName = type.toString().replaceAll(clazz.getCanonicalName(), "").replaceAll("\\<", "").replaceAll("\\>", "");

      if (itemName.indexOf(",") == -1) {
        Class<?> itemCls = Class.forName(itemName);
        itemClassO_[0] = itemCls;
      } else {
        String[] itemArray = itemName.split(",");
        if (itemArray != null && itemArray.length == 2) {
          itemClassO_[0] = Class.forName(itemArray[0].replaceFirst(" ", ""));
          itemClassO_[1] = Class.forName(itemArray[1].replaceFirst(" ", ""));
        }
      }

      // Class<?> itemCls = Class.forName(itemName);
      // this.setItemClass(itemCls);
      this.setItemClass_(itemClassO_);
      this.setContainerClass(clazz);
      this.setIsGeneric(true);

      String sn = "";
      if (value instanceof Out) {
        sn = itemName.substring(itemName.lastIndexOf(".") + 1);
      } else {
        sn = clazz.getCanonicalName();
        sn = sn.substring(sn.lastIndexOf(".") + 1);

        if (itemName.indexOf(",") == -1) {
          itemName = itemName.substring(itemName.lastIndexOf(".") + 1);
          sn = sn + "<" + itemName + ">";
        } else {
          String[] genericItem = type.toString().replaceAll(clazz.getCanonicalName(), "").replaceAll("\\<", "").replaceAll("\\>", "")
              .split(",");
          sn = sn + "<" + genericItem[0].substring(genericItem[0].lastIndexOf(".") + 1) + ","
              + genericItem[1].substring(genericItem[1].lastIndexOf(".") + 1) + ">";
        }
      }
      this.setSimpleName(sn);
    } else {
      this.setIsGeneric(false);
      // this.setItemClass(clazz);
      itemClassO_[0] = clazz;
      this.setItemClass_(itemClassO_);
      this.setItemClass(clazz);
      this.setSimpleName(clazz.getSimpleName());
    }
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }

  public ParaType getParaType() {
    return paraType;
  }

  public void setParaType(ParaType paraType) {
    this.paraType = paraType;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public Class<?> getContainerClass() {
    return containerClass;
  }

  public void setContainerClass(Class<?> containerClass) {
    this.containerClass = containerClass;
  }

  public Class<?> getItemClass() {
    return itemClass;
  }

  public void setItemClass(Class<?> itemClass) {
    this.itemClass = itemClass;
  }

  public String getSimpleName() {
    return simpleName;
  }

  public void setSimpleName(String simpleName) {
    this.simpleName = simpleName;
  }

  public boolean isIsGeneric() {
    return isGeneric;
  }

  public void setIsGeneric(boolean isGeneric) {
    this.isGeneric = isGeneric;
  }

  public Class<?>[] getItemClass_() {
    return itemClass_;
  }

  public void setItemClass_(Class<?>[] itemClass_) {
    this.itemClass_ = itemClass_;
  }

}