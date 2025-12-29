package com.myproject.todoapp.payload.messages;

public class ErrorMessages {
    public static final String USERNAME_NOT_FOUND = "Username: %s not found.";
    public static final String TODO_ID_NOT_FOUND = "No todo was found by ID: %s";
    public static final String ACTION_NOT_ALLOWED = "This action is not allowed.";
    public static final String TODO_OWNER_DIFFERENT = "The todo owner is a different user.";
    public static final String TODO_ALREADY_NOT_DELETED = "The todo is already not deleted. Undo aborted.";
    public static final String USERNAME_EMAIL_ALREADY_EXISTS = "Username and email both already exist. Please try again";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists. Please try another username.";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists. Please try another email address.";
    public static final String USER_NOT_FOUND_BY_ID = "User not found by ID: %s";
    public static final String BUILT_IN_ADMIN_CANNOT_BE_UPDATED = "Built-in admin cannot be updated";
    public static final String PASSWORD_INVALID = "The password is either wrong or invalid. Try again.";
    public static final String ILLEGAL_REFRESH_TOKEN = "Refresh token is either invalid or expired. Please login " +
            "again.";
}
