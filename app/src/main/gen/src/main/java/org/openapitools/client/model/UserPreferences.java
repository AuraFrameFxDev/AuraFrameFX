/*
 * AuraFrameFX Ecosystem API
 * A comprehensive API for interacting with the AuraFrameFX AI Super Dimensional Ecosystem. Provides access to generative AI capabilities, system customization, user management, and core application features. 
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: support@auraframefx.com
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.openapitools.client.model;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openapitools.client.JSON;

/**
 * UserPreferences
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-06-23T00:56:53.569863-06:00[America/Denver]", comments = "Generator version: 7.7.0")
public class UserPreferences {
  public static final String SERIALIZED_NAME_THEME_ID = "themeId";
  @SerializedName(SERIALIZED_NAME_THEME_ID)
  private String themeId;

  public static final String SERIALIZED_NAME_LANGUAGE = "language";
  @SerializedName(SERIALIZED_NAME_LANGUAGE)
  private String language;

  public static final String SERIALIZED_NAME_NOTIFICATIONS_ENABLED = "notificationsEnabled";
  @SerializedName(SERIALIZED_NAME_NOTIFICATIONS_ENABLED)
  private Boolean notificationsEnabled;

  public UserPreferences() {
  }

  public UserPreferences themeId(String themeId) {
    this.themeId = themeId;
    return this;
  }

  /**
   * Get themeId
   * @return themeId
   */
  @javax.annotation.Nullable
  public String getThemeId() {
    return themeId;
  }

  public void setThemeId(String themeId) {
    this.themeId = themeId;
  }


  public UserPreferences language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  @javax.annotation.Nullable
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }


  public UserPreferences notificationsEnabled(Boolean notificationsEnabled) {
    this.notificationsEnabled = notificationsEnabled;
    return this;
  }

  /**
   * Get notificationsEnabled
   * @return notificationsEnabled
   */
  @javax.annotation.Nullable
  public Boolean getNotificationsEnabled() {
    return notificationsEnabled;
  }

  public void setNotificationsEnabled(Boolean notificationsEnabled) {
    this.notificationsEnabled = notificationsEnabled;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserPreferences userPreferences = (UserPreferences) o;
    return Objects.equals(this.themeId, userPreferences.themeId) &&
        Objects.equals(this.language, userPreferences.language) &&
        Objects.equals(this.notificationsEnabled, userPreferences.notificationsEnabled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(themeId, language, notificationsEnabled);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserPreferences {\n");
    sb.append("    themeId: ").append(toIndentedString(themeId)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    notificationsEnabled: ").append(toIndentedString(notificationsEnabled)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }


  public static HashSet<String> openapiFields;
  public static HashSet<String> openapiRequiredFields;

  static {
    // a set of all properties/fields (JSON key names)
    openapiFields = new HashSet<String>();
    openapiFields.add("themeId");
    openapiFields.add("language");
    openapiFields.add("notificationsEnabled");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

  /**
   * Validates the JSON Element and throws an exception if issues found
   *
   * @param jsonElement JSON Element
   * @throws IOException if the JSON Element is invalid with respect to UserPreferences
   */
  public static void validateJsonElement(JsonElement jsonElement) throws IOException {
      if (jsonElement == null) {
        if (!UserPreferences.openapiRequiredFields.isEmpty()) { // has required fields but JSON element is null
          throw new IllegalArgumentException(String.format("The required field(s) %s in UserPreferences is not found in the empty JSON string", UserPreferences.openapiRequiredFields.toString()));
        }
      }

      Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
      // check to see if the JSON string contains additional fields
      for (Map.Entry<String, JsonElement> entry : entries) {
        if (!UserPreferences.openapiFields.contains(entry.getKey())) {
          throw new IllegalArgumentException(String.format("The field `%s` in the JSON string is not defined in the `UserPreferences` properties. JSON: %s", entry.getKey(), jsonElement.toString()));
        }
      }
        JsonObject jsonObj = jsonElement.getAsJsonObject();
      if ((jsonObj.get("themeId") != null && !jsonObj.get("themeId").isJsonNull()) && !jsonObj.get("themeId").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `themeId` to be a primitive type in the JSON string but got `%s`", jsonObj.get("themeId").toString()));
      }
      if ((jsonObj.get("language") != null && !jsonObj.get("language").isJsonNull()) && !jsonObj.get("language").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `language` to be a primitive type in the JSON string but got `%s`", jsonObj.get("language").toString()));
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!UserPreferences.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'UserPreferences' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<UserPreferences> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(UserPreferences.class));

       return (TypeAdapter<T>) new TypeAdapter<UserPreferences>() {
           @Override
           public void write(JsonWriter out, UserPreferences value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             elementAdapter.write(out, obj);
           }

           @Override
           public UserPreferences read(JsonReader in) throws IOException {
             JsonElement jsonElement = elementAdapter.read(in);
             validateJsonElement(jsonElement);
             return thisAdapter.fromJsonTree(jsonElement);
           }

       }.nullSafe();
    }
  }

  /**
   * Create an instance of UserPreferences given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of UserPreferences
   * @throws IOException if the JSON string is invalid with respect to UserPreferences
   */
  public static UserPreferences fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, UserPreferences.class);
  }

  /**
   * Convert an instance of UserPreferences to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

