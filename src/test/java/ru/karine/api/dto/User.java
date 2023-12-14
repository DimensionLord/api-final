

package ru.karine.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class User {

  private Long id;

  private String username;

  private String firstName;


  private String lastName;

  private String email;


  private String password;


  private String phone;


  private Integer userStatus;

}

