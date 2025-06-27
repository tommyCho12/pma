package com.pma.entities;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user_accounts")
public class UserAccount
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ua_generator")
    @SequenceGenerator(name = "ua_generator", sequenceName = "user_accounts_seq", allocationSize = 1)
    @Column(name = "user_id")
    private long userId;

    @Column(name = "username")
    @NotNull
    @Size(min = 3, max = 20)
    private String userName;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

    private String role;

    private boolean enabled = true;

    public UserAccount(){};

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    public long getUserId()
    {
        return userId;
    }

    public void setUserId(long userId)
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
