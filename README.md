SmartBudget: "Gestiona tus finanzas de manera inteligente" 
SmartBudget es una aplicación móvil de gestión de finanzas personales, desarrollada con un enfoque prioritario en la estabilidad, la integridad de los datos y una experiencia de usuario fluida, independientemente del estado de la red.

Este proyecto sirve como una implementación canónica de las mejores prácticas de desarrollo en Android moderno.

Enfoque y Propósito
El objetivo principal de SmartBudget es proporcionar un control financiero confiable:

Registro Holístico: Manejo de las entidades clave: Usuarios, Gastos, Ingresos, Categorías y Metas.

Resiliencia a la Red: Garantía de operación continua bajo el paradigma Offline First.

Estado Predecible: Adopción de MVI para un state management sin ambigüedades.

Ingeniería de Software: Patrones Fundamentales
La robustez del proyecto se cimienta sobre principios de ingeniería avanzados, asegurando escalabilidad y facilidad de mantenimiento.

1. Arquitectura Limpia (Clean Architecture)
El proyecto utiliza una estricta separación de capas, aislando la lógica de negocio de las preocupaciones de la plataforma.

Dominio (domain): Corazón de la aplicación. Contiene las interfaces de repositorio y los use cases (*UseCase), que encapsulan las reglas de negocio, garantizando su independencia de la infraestructura.

Datos (data): Implementa las interfaces del dominio. Aquí se encuentra la lógica de Single Source of Truth (SSOT), manejando la conmutación entre la fuente local (Room) y la remota (API).

Presentación (presentation): La capa de UI (Jetpack Compose) y los *ViewModel.

2. Single Source of Truth (SSOT) & Offline First
Para garantizar la coherencia de los datos en entornos conectados y desconectados, la Base de Datos Local (Room) es designada como el SSOT.

Flujo de Lectura: Toda la UI se suscribe a los Flows de la base de datos local.

Flujo de Escritura: Los repositorios primero persisten el cambio en Room (marcando la entidad como isPendingCreate o isPendingUpdate) y luego inician el proceso de sincronización.

Sincronización: El sistema depende de WorkManager (*SyncWorker) para ejecutar la lógica de reintento y envío de datos pendientes, manteniendo la interfaz de usuario reactiva y libre de bloqueos.

3. MVI y Flujo de Datos Unidireccional (UDF)
La capa de UI utiliza el patrón Model-View-Intent para crear una interfaz predecible y reactiva.

Intents (*UiEvent): Las acciones del usuario se envían como eventos al ViewModel.

Estado (*UiState): El ViewModel procesa el evento y emite un estado inmutable único a través de un StateFlow.

Reactividad: La UI (Compose) simplemente consume este estado y se reconstruye, eliminando efectos secundarios y race conditions.

Contribuciones y Desarrollo
Las Pull Requests son bienvenidas. Se espera que cualquier contribución se adhiera estrictamente a los patrones arquitectónicos Clean Architecture y MVI.

Desarrollado por: Esmailin Martínez Licencia
