package com.isaacdev.anchor.data.database

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {

    private const val SUPABASE_URL = "https://pgzdukytiwarbisqcpcl.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBnemR1a3l0aXdhcmJpc3FjcGNsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU0MTA1OTYsImV4cCI6MjA2MDk4NjU5Nn0.9oh8p8td0c_YxlR-6MvWPXv6T5hOrTAXLRilXdpX1wE"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
        install(Auth)
    }
}