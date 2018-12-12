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

import org.springframework.data.annotation.Id;

/**
 * Class representing an end-user
 */
public class ApplicationUser {
  @Id
  public String id;

  public String username;
  public String password;
  public boolean isAdmin;

	/**
	* Default empty ApplicationUser constructor
	*/
	public ApplicationUser() {
		super();
	}

	/**
	* Returns value of id
	* @return
	*/
	public String getId() {
		return id;
	}

	/**
	* Returns value of username
	* @return
	*/
	public String getUsername() {
		return username;
	}

	/**
	* Returns value of password
	* @return
	*/
	public String getPassword() {
		return password;
	}

	/**
	* Returns value of isAdmin
	* @return
	*/
	public boolean isAdmin() {
		return this.isAdmin;
	}
}
