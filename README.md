# Monitoreo de Sensores en Tiempo Real 📡

Aplicación Android nativa (Java) que monitorea sensores en tiempo real usando **Firebase Realtime Database**. Muestra los valores de temperatura, humedad, presión y velocidad conforme cambian en la base de datos, y permite escribir (setear) nuevos valores de temperatura y humedad desde la propia app.

> Proyecto desarrollado para la materia **Aplicaciones Móviles** (Ing. Cristian Zambrano).

## Funcionalidades

- **Monitoreo en tiempo real** de 4 sensores mediante `ValueEventListener`: cualquier cambio en la base de datos se refleja en pantalla al instante, sin recargar.
  - 🌡️ Temperatura (°C)
  - 💧 Humedad (%)
  - 📶 Presión (hPa)
  - ⚡ Velocidad (km/h)
- **Seteo de valores**: campos de entrada numérica con botón "Set" para escribir nuevos valores de temperatura y humedad directamente en Firebase.
- **Validación de entrada**: si el campo está vacío o no es numérico, se muestra un Toast en lugar de romper la app.
- **Modo oscuro permanente**: la app fuerza el tema oscuro (`AppCompatDelegate.MODE_NIGHT_YES`) con paleta lavanda oscura.

## Tecnologías

| Componente | Detalle |
|---|---|
| Lenguaje | Java (100 %, sin Kotlin) |
| UI | XML con `ScrollView` + `LinearLayout`, iconos VectorDrawable de Material Design |
| Backend | Firebase Realtime Database (proyecto `fir-monitoreos13`) |
| Dependencias Firebase | `firebase-bom` + `firebase-database` |
| minSdk / targetSdk | 24 / 36 |
| Build | Gradle con Kotlin DSL y catálogo de versiones (`libs.versions.toml`) |

## Estructura de la base de datos

```
https://fir-monitoreos13-default-rtdb.firebaseio.com/
└── sensores/
    ├── fecha        (string)
    ├── humedad      (número)   ← lectura y escritura
    ├── presion      (número)   ← solo lectura
    ├── temperatura  (número)   ← lectura y escritura
    └── velocidad    (número)   ← solo lectura
```

## Estructura del proyecto

```
app/src/main/
├── java/com/uteq/monitoreofirebases13/
│   └── MainActivity.java          # Toda la lógica: referencias, listener genérico y botones Set
├── res/
│   ├── layout/activity_main.xml   # Pantalla única: filas de monitoreo y de seteo
│   └── drawable/
│       ├── ic_temperatura.xml     # Termómetro (Material: device_thermostat)
│       ├── ic_humedad.xml         # Gota (Material: water_drop)
│       ├── ic_presion.xml         # Ondas (Material: wifi)
│       ├── ic_velocidad.xml       # Velocímetro (Material: speed)
│       └── button_rounded.xml     # Botón píldora morado de los "Set"
└── AndroidManifest.xml            # Permiso INTERNET
```

## Cómo funciona (resumen del código)

1. **Referencias** — en `onCreate` se obtiene la instancia de la base y una `DatabaseReference` por sensor:
   ```java
   database = FirebaseDatabase.getInstance();
   temperaturaRef = database.getReference("sensores/temperatura");
   ```
2. **Lectura en tiempo real** — un método genérico crea el listener y se adjunta a cada referencia con su unidad:
   ```java
   temperaturaRef.addValueEventListener(setListener(valorTemperatura, "°C"));
   ```
   En `onDataChange` el TextView se actualiza con `snapshot.getValue().toString() + " " + unidad`.
3. **Escritura** — los botones "Set" (vía `android:onClick`) parsean el EditText y escriben:
   ```java
   temperaturaRef.setValue(Float.parseFloat(setvalorTemperatura.getText().toString()));
   ```

## Configuración para ejecutar

1. Clonar/abrir el proyecto en **Android Studio**.
2. Descargar el archivo **`google-services.json`** desde la consola de Firebase del proyecto `fir-monitoreos13` (⚙️ Configuración del proyecto → Tus apps → app Android) y colocarlo en la carpeta **`app/`**.
   - Importante: debe ser un JSON descargado *después* de crear la Realtime Database, para que incluya la clave `firebase_url`. Sin ella la app crashea al abrir.
3. Verificar en la consola (Realtime Database → Reglas) que las reglas permitan lectura/escritura; para pruebas:
   ```json
   { "rules": { ".read": true, ".write": true } }
   ```
   Si están en modo test con fecha de expiración, revisar que no haya vencido.
4. Sincronizar Gradle y ejecutar en un emulador o dispositivo con conexión a internet (API 24+).

## Pruebas rápidas por REST

Ver el estado actual de los sensores:
```bash
curl -s "https://fir-monitoreos13-default-rtdb.firebaseio.com/sensores.json"
```
Cambiar un valor desde fuera de la app (se reflejará en pantalla al instante):
```bash
curl -s -X PUT -d '42' "https://fir-monitoreos13-default-rtdb.firebaseio.com/sensores/temperatura.json"
```

## Notas

- Los valores se muestran sin formateo decimal (comportamiento definido por el material del curso): un valor `250.899` se muestra tal cual.
- Las reglas abiertas de la base son solo para fines académicos; en producción se usaría Firebase Authentication y reglas restrictivas.
- Este alcance no incluye ubicación ni Google Maps (corresponde a una app posterior de la materia).
