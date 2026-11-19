
package com.extracreditstudios.thelastsemester.shared.messages;

public class ChangePassword {
    public record Request(String old_password, String new_password) {}
}
