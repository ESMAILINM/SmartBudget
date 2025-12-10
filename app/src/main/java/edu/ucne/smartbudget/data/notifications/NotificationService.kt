package edu.ucne.smartbudget.data.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.ucne.smartbudget.MainActivity
import edu.ucne.smartbudget.R
import javax.inject.Inject

class NotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val CHANNEL_ID = "metas_channel"
        const val CHANNEL_NAME = "Metas Completadas"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones cuando se completa una meta de ahorro"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }


    fun showMetaCompletedNotification(metaNombre: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("NotificationService", "NO TIENES PERMISO DE NOTIFICACIONES.")
            return
        }

        val notificationManager = NotificationManagerCompat.from(context)
        if (!notificationManager.areNotificationsEnabled()) {
            Log.e("NotificationService", "Las notificaciones están desactivadas para esta App.")
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("¡Felicidades! 🎉")
            .setContentText("Has completado tu meta: $metaNombre")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            notificationManager.notify(metaNombre.hashCode(), builder.build())
        } catch (e: SecurityException) {
            Log.e("NotificationService", "Error de seguridad: ${e.message}")
        } catch (e: Exception) {
            Log.e("NotificationService", "Error desconocido: ${e.message}")
        }
    }
}
