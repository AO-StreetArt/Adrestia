/*
Apache2 License Notice
Copyright 2018 Alex Barry
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.ao.adrestia.model;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;

/**
* Class representing an end-user.
*/
public class ApplicationUser {
  @Id
  public String id;

  public String username;
  public String password;
  public String email;
  public boolean isAdmin;
  public boolean isActive;
  public ArrayList<String> favoriteProjects;
  public ArrayList<String> favoriteScenes;

  /**
  * Default empty ApplicationUser constructor.
  */
  public ApplicationUser() {
    super();
  }

  /**
  * Default ApplicationUser constructor.
  */
  public ApplicationUser(String id,
      String username, String password,
      String email, boolean isAdmin,
      boolean isActive, ArrayList<String> favoriteProjects,
      ArrayList<String> favoriteScenes) {
    super();
    this.id = id;
    this.username = username;
    this.password = password;
    this.email = email;
    this.isAdmin = isAdmin;
    this.isActive = isActive;
    this.favoriteProjects = favoriteProjects;
    this.favoriteScenes = favoriteScenes;
  }

  /**
  * Returns value of id.
  * @return The ID of the user.
  */
  public String getId() {
    return id;
  }

  /**
  * Sets new value of id.
  * @param id The new ID of the user.
  */
  public void setId(String id) {
    this.id = id;
  }

  /**
  * Returns value of username.
  * @return The username of the user.
  */
  public String getUsername() {
    return username;
  }

  /**
  * Sets new value of username.
  * @param username The new username for the user.
  */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
  * Returns value of password.
  * @return The password of the user.
  */
  public String getPassword() {
    return password;
  }

  /**
  * Sets new value of password.
  * @param password The new password of the user.
  */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
  * Returns value of email.
  * @return The email of the user.
  */
  public String getEmail() {
    return email;
  }

  /**
  * Sets new value of email.
  * @param email The new Email for the user.
  */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
  * Returns value of isAdmin.
  * @return True if the user has administrator access.
  */
  public boolean getIsAdmin() {
    return isAdmin;
  }

  /**
  * Sets new value of isAdmin.
  * @param isAdmin Boolean value to update the 'isAdmin' attribute.
  */
  public void setIsAdmin(boolean isAdmin) {
    this.isAdmin = isAdmin;
  }

  /**
  * Returns value of isActive.
  * @return True if the user is currently active.
  */
  public boolean getIsActive() {
    return isActive;
  }

  /**
  * Sets new value of isActive.
  * @param isActive Boolean value which will update the 'isActive' attribute.
  */
  public void setIsActive(boolean isActive) {
    this.isActive = isActive;
  }

  /**
  * Returns value of favoriteProjects.
  * @return a list of ID's corresponding to projects.
  */
  public List<String> getFavoriteProjects() {
    return favoriteProjects;
  }

  /**
  * Sets new value of favoriteProjects.
  * @param favoriteProjects The new list of Project ID's to store as favorites.
  */
  public void setFavoriteProjects(ArrayList<String> favoriteProjects) {
    this.favoriteProjects = favoriteProjects;
  }

  /**
  * Returns value of favoriteScenes.
  * @return a list of ID's corresponding to Scenes.
  */
  public List<String> getFavoriteScenes() {
    return favoriteScenes;
  }

  /**
  * Sets new value of favoriteScenes.
  * @param favoriteScenes The new list of Scene ID's to store as favorites.
  */
  public void setFavoriteScenes(ArrayList<String> favoriteScenes) {
    this.favoriteScenes = favoriteScenes;
  }
}
