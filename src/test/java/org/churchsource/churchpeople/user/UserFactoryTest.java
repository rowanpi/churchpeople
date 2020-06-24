package org.churchsource.churchpeople.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.churchsource.churchpeople.user.CPUserDetails.aCPUserDetails;
import static org.churchsource.churchpeople.user.Privilege.aPrivilege;
import static org.churchsource.churchpeople.user.Role.aRole;
import static org.churchsource.churchpeople.user.UserFullViewModel.aUserFullViewModel;
import static org.churchsource.churchpeople.user.helpers.UserFullViewModelMatcher.hasSameStateAsUserFullViewModel;
import static org.churchsource.churchpeople.user.helpers.CPUserDetailsMatcher.hasSameStateAsCPUserDetails;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserFactoryTest {

  @InjectMocks
  private UserFactory userFactory;

  @Test
  public void testCreateUserFullViewModelFromEntity_shouldReturnCorrectFullViewModel() {
    List<Role> roles = new ArrayList<Role>();
    List<Privilege> privileges = new ArrayList<Privilege>();
    privileges.add(aPrivilege().name("priv1").build());
    privileges.add(aPrivilege().name("priv2").build());
    roles.add(aRole().name("role_1").privileges(privileges).build());

    CPUserDetails aCPUserDetails = aCPUserDetails().id(1L).username("Joe").password("Bar").isEnabled(true).deleted(false).roles(roles).build();

    UserFullViewModel expectedUserFullViewModel = aUserFullViewModel().id(1L).username("Joe").password("Bar").isEnabled(true).isExpired(false).isLocked(false).roles(roles).build();

    UserFullViewModel createdUserFullViewModel = userFactory.createUserFullViewModelFromEntity(aCPUserDetails);

    assertThat(createdUserFullViewModel, hasSameStateAsUserFullViewModel(expectedUserFullViewModel));
  }

  @Test
  public void testCreateUserFullViewModelFromEntityWithoutRoles_shouldReturnCorrectFullViewModel() {
    CPUserDetails aCPUserDetails = aCPUserDetails().id(1L).username("Joe").password("Bar").isEnabled(true).deleted(false).roles(null).build();

    UserFullViewModel expectedUserFullViewModel = aUserFullViewModel().id(1L).username("Joe").password("Bar").isEnabled(true).isExpired(false).isLocked(false).roles(null).build();

    UserFullViewModel createdUserFullViewModel = userFactory.createUserFullViewModelFromEntity(aCPUserDetails);

    assertThat(createdUserFullViewModel, hasSameStateAsUserFullViewModel(expectedUserFullViewModel));
  }

  @Test
  public void testCreateCPUserEntityFromBackingForm_shouldReturnCorrectEntity() {

    UserFactory uf = Mockito.spy(userFactory);

    List<Role> roles = new ArrayList<Role>();
    roles.add(aRole().name("role_1").build());
    roles.add(aRole().name("role_2").build());

    String password = "password";
    String encodedPassword = "EncodedPassword";
    CPUserDetails expectedCPUserDetails = aCPUserDetails().id(1L).username("Joe").password(encodedPassword).isEnabled(true).deleted(false).roles(roles).build();
    UserBackingForm userBackingForm = UserBackingForm.aUserBackingForm().id(1L).username("Joe").password(password).roles(roles).build();

    when(uf.getEncodedPassword(password)).thenReturn(encodedPassword);
    CPUserDetails createdUserEntity = uf.createUserEntity(userBackingForm);

    assertThat(createdUserEntity, hasSameStateAsCPUserDetails(expectedCPUserDetails));
  }
}