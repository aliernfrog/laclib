package com.aliernfrog.laclib.util

// None of the methods here are currently used by the library
// as they are mostly designed to be used outside of the library.

/**
 * Contains various helper methods.
 */
@Suppress("unused")
class LACHelperUtil {
    companion object {

        /**
         * Builds a role string based on [name] and [color].
         * @return Role string, empty string if [name] is blank
         */
        fun buildRoleString(name: String, color: String? = null): String? {
            if (name.isBlank()) return null
            var role = name
            if (!role.contains("[")) role = "[$role"
            if (!role.contains("]")) role = "$role]"
            if (!color.isNullOrBlank()) role = "<color=$color>$role</color>"
            return role
        }

        /**
         * Converts [role] to a HTML string.
         * Example: <color=red>[ROLE]</color> -> <font color=red>[ROLE]</font>
         */
        @Suppress("KDocUnresolvedReference")
        fun roleStringToHtml(role: String): String {
            return role.replace("<color", "<font color").replace("</color>", "</font>")
        }

    }
}