package org.ilite.frc.common.types;

public enum ESupportedTypes  {
  LONG(Long.class),
  DOUBLE(Double.class),
  BOOLEAN(Boolean.class),
  STRING(String.class),
  INTEGER(Integer.class),
  UNSUPPORTED(Object.class);
  
  public final Class<?> type;
  
  private ESupportedTypes(Class<?> pType) {
    type = pType;
  }

  public static ESupportedTypes fromType(Class<?> pType) {
    for(ESupportedTypes type : values()) {
      if(type.type.equals(pType)) return type;
    }
    return UNSUPPORTED;
  }
}
