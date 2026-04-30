## Plan: Fix missing signup button label

TL;DR - The login screen signup button has inline text but poor styling may make it invisible. Update `activity_login.xml` to use a clear string resource and the same primary button style as the login button.

**Steps**
1. Edit `app/src/main/res/layout/activity_login.xml`.
   - Change the signup button `android:text` to `@string/signup` or `Sign Up`.
   - Change `android:textColor` to `#FFFFFF` for contrast.
   - Change `android:background` to `@drawable/bg_button_primary` to make the button visible and consistent.
2. Keep the runtime code in `app/src/main/java/com/stylica/makeupclothing/ui/LoginActivity.kt` unchanged unless there is additional logic clearing the text.
3. Verify the login screen in the emulator or device and confirm the signup button now shows the label.

**Relevant files**
- `app/src/main/res/layout/activity_login.xml`
- `app/src/main/res/drawable/bg_button_primary.xml`
- `app/src/main/res/values/strings.xml`

**Verification**
1. Open the login screen and verify the signup button displays "Sign Up" or the string resource text.
2. Confirm the button is not just a pink bar and is visible next to the login button.

**Decisions**
- Use the existing `@drawable/bg_button_primary` style for visibility.
- Prefer `@string/signup` for localization and consistency.
