/*
Apache2 License Notice
Copyright 2017 Alex Barry

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

package adrestia;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


/**
* Adrestia Utility Provider.
* Provides access to various utility methods.
*/
@Service
public interface UtilityProviderInterface {

  /**
  * Translate an Error Code from Crazy Ivan into an HTTP Error Code.
  * @param dvsErrorCode An Error Code from a DVS Service (Crazy Ivan, CLyman)
  * @return An HTTP Status which can be returned by Adrestia
  */
  public HttpStatus translateDvsError(int dvsErrorCode);

  /**
  * Return a psuedo-random integer.
  */
  public int getRandomInt(int max);

}
