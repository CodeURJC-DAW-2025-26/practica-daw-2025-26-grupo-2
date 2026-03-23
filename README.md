# ZENDA Shop

## 👥 Miembros del Equipo
| Nombre y Apellidos | Correo URJC | Usuario GitHub |
|:--- |:--- |:--- |
| Samuel Melián Benito | s.melian.2022@alumnos.urjc.es | SamuelMelian |
| Víctor Navarro Santos | v.navarros.2022@alumnos.urjc.es | victor04san7os |
| Alejandro Lefebvre Valiente | a.lefebvre.2022@alumnos.urjc.es | Lefebvre66 |
| Jorge Padilla Rodríguez | j.padilla.2021@alumnos.urjc.es | Jorge-PR |
| Sara Guillén Martínez | s.guillenm.2022@alumnos.urjc.es | saraguillenmtz |

---

## 🎭 **Preparación 1: Definición del Proyecto**

### **Descripción del Tema**
La aplicación web pertenece al sector del comercio electrónico de moda y está orientada a la venta online de ropa. Permite a los usuarios explorar un catálogo de prendas, gestionar un carrito de compra (pedido en progreso), realizar pedidos y dejar opiniones sobre los productos adquiridos. Como valor añadido, la aplicación ofrece estadísticas de ventas e ingresos. Aporta valor al usuario ofreciendo una experiencia de compra sencilla y personalizada, con facturación automática en PDF y recomendaciones basadas en sus preferencias de compra.


### **Entidades**
1. **Usuario**: cliente registrado que navega, compra y opina en la plataforma.  
2. **Prenda**: producto de ropa disponible en el catálogo.  
3. **Pedido**: registro de una compra realizada por un usuario, que incluye una o varias prendas y almacena la información necesaria para la facturación.
4. **Opinión**: valoración y comentario de un usuario sobre una prenda.


**Relaciones entre entidades:**
- **Usuario - Pedido:** un usuario puede realizar múltiples pedidos (**1:N**).  
- **Pedido - Prenda:** un pedido puede contener múltiples prendas y una prenda puede aparecer en múltiples pedidos (**N:M**).  
- **Usuario - Opinión:** un usuario puede escribir múltiples opiniones (**1:N**).  
- **Prenda - Opinión:** una prenda puede recibir múltiples opiniones (**1:N**).

### **Permisos de los Usuarios**
Describir los permisos de cada tipo de usuario e indicar de qué entidades es dueño:

* **Usuario Anónimo**:  
  - **Permisos:** visualización del catálogo de prendas, búsqueda y filtrado de productos, registro en la plataforma.  
  - **Propiedad:** no es dueño de ninguna entidad.

* **Usuario Registrado**:  
  - **Permisos:** gestión de su perfil, edición de pedido (en estado en progreso = "carrito"), realización de pedidos, generación y descarga de facturas en PDF para un pedido realizado, creación y edición de opiniones sobre prendas adquiridas.  
  - **Propiedad:** su perfil de usuario, su pedido en progreso y sus opiniones.

* **Administrador**:  
  - **Permisos:** gestión completa (CRUD) de usuarios, prendas, pedidos (en estado realizado) y opiniones, visualización de estadísticas de ventas e ingresos.  
  - **Propiedad:** es dueño de las prendas y los pedidos realizados.


### **Imágenes**
Indicar qué entidades tendrán asociadas una o varias imágenes:

- **Usuario**: una imagen de avatar opcional por usuario.
- **Prenda**: una imagen por prenda para su visualización en el catálogo.


### **Gráficos**
Indicar qué información se mostrará usando gráficos y de qué tipo serán:

- **Gráfico 1**: número de ventas por periodo de tiempo — gráfico de barras.
- **Gráfico 2**: ingresos totales por periodo de tiempo — gráfico de líneas.
- **Gráfico 3**: valor medio de los pedidos (ticket medio) por periodo de tiempo — gráfico de líneas.


### **Tecnología Complementaria**
Indicar qué tecnología complementaria se empleará:

- **Generación de facturas en PDF** de cada pedido realizada por el usuario, incluyendo el detalle de las prendas adquiridas, precios, impuestos y datos del cliente.


### **Algoritmo o Consulta Avanzada**
Indicar cuál será el algoritmo o consulta avanzada que se implementará:

- **Algoritmo/Consulta:** generación de ofertas personalizadas para el usuario.
- **Descripción:** el sistema analiza los pedidos realizadas por el usuario durante el último mes, identifica la categoría de productos más comprada y muestra tres ofertas basadas en dicha categoría.
- **Alternativa:** consulta avanzada que agrupa los pedidos del último mes por usuario y categoría, calculando la frecuencia de pedidos para determinar la preferencia principal.

Se valorará en el momento indicado realizar una consulta más avanzada con recomendaciones personalizadas basadas en lo que compran los usuarios que más se parecen a ti.

---

## 🛠 **Preparación 2: Maquetación de páginas con HTML y CSS**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en Youtube](https://www.youtube.com/watch?v=rHKQmr89hOA)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Diagrama de Navegación**
Diagrama que muestra cómo se navega entre las diferentes páginas de la aplicación:

![Diagrama de Navegación](images/navigation_diagram.jpg)

> [Descripción opcional del flujo de navegación: Ej: "El usuario puede acceder desde la página principal a todas las secciones mediante el menú de navegación. Los usuarios anónimos solo tienen acceso a las páginas públicas, mientras que los registrados pueden acceder a su perfil y panel de usuario."]

### **Capturas de Pantalla y Descripción de Páginas**

#### **1. Página Principal (usuario no registrado)**
![Index](images/image-1.png)
>La página principal representa el punto de inicio de la aplicación y está diseñada para ofrecer una visión clara y ordenada del catálogo de productos. Los artículos se muestran en un diseño en cuadrícula responsive, donde cada producto incluye su imagen, nombre y precio y un acceso rápido al detalle de cada prenda. En la parte superior se encuentra una barra de navegación fija que permite volver al inicio en todo momento y acceder a las opciones de registro e inicio de sesión para usuarios no autenticados. Además, se ha incorporado un sistema de filtrado desplegable que permite buscar y ordenar los productos por categoría, rango de precios o popularidad. El footer contiene enlaces informativos y acceso a redes sociales.

#### **2. Página de Registro**
![Register](images/image.png)

>La página de registro permite al usuario crear una cuenta mediante un formulario organizado. Incluye la opción de subir una imagen de perfil (mostrando un avatar por defecto), además de los campos principales necesarios para el alta: nombre, email, contraseña y confirmación de contraseña, junto con la dirección. Para mejorar la usabilidad, cada campo está acompañado de iconos dentro de los input-group, lo que facilita identificar rápidamente qué información se solicita. Finalmente, se ofrecen accesos directos tanto para completar el registro (“Crear cuenta”) como para volver a la tienda o ir a la pantalla de inicio de sesión si el usuario ya dispone de cuenta.

#### **3. Iniciar sesión**
![Log in](images/image_login.png)

>La página de inicio de sesión permite acceder a la aplicación mediante un formulario: email y contraseña. Los campos incluyen iconos en cada input para facilitar la lectura, además de validaciones básicas (formato de email y longitud mínima de contraseña) para evitar errores comunes. Además, se incluyen dos accesos diferenciados: uno para entrar como usuario registrado y otro para entrar como administrador. Sin embargo, en la próxima práctica habrá solamente un botón de entrar y se iniciará sesión como usuario normal o administrador dependiendo de las credenciales introducidas. También se añade un enlace para volver a la tienda sin iniciar sesión y un acceso directo a la página de registro para usuarios nuevos.

#### **4. Página Principal (usuario registrado)**
![User home 1](images/image_userhome1.png)
![User home 2](images/image_userhome2.png)

>La página principal para usuarios registrados mantiene la estructura del catálogo, pero añade funcionalidades pensadas para una experiencia más personalizada. En la barra superior se incorporan accesos directos al carrito y un menú desplegable de perfil, desde el que el usuario puede gestionar sus datos, consultar sus pedidos o cerrar sesión. Se incluye una sección de “Ofertas para ti” al inicio, apartado de recomendaciones u ofertas destacadas. A continuación, se mantiene el listado general de artículos con el mismo sistema de filtrado desplegable (búsqueda, categoría, rango de precio y ordenación). El footer conserva enlaces informativos y redes sociales.

#### **5. Página de detalle producto ofertado**
![Garment orffer 1](images/garment_offer1.png)
![Garment offer 2](images/garment_offer2.png)

> La página de detalle muestra toda la información relevante de un producto de forma más completa para su compra. En la parte superior se mantiene la navegación del usuario registrado (home, carrito y menú de perfil) y se añade un botón de “Volver” para regresar al catálogo. El contenido principal incluye la imagen del producto, su referencia y el precio, destacando las ofertas mediante el precio tachado, el precio rebajado y un mensaje informativo del descuento. Los botones de editar y borrar serán solo visibles para el administrador. Además, se permite seleccionar talla y cantidad antes de añadir el artículo al carrito. Finalmente, se incorpora una sección de comentarios con formulario de valoración y listado de reseñas, incluyendo opciones de editar y borrar (estos dos últimos solamente también serán visibles solamente para el usuario).

#### **6. Página de Detalle de Producto (sin oferta)**

![Garment 1](images/garment1.png)
![Garment 2](images/garment2.png)

> Esta página muestra el detalle completo de un producto cuando no está en promoción, manteniendo la misma estructura de navegación para usuarios registrados(home, carrito y menú de perfil) y un botón de “Volver”, dado que los no registrados solamente podrán ver el producto y las opiniones, pero no podrán ni comprar ni añadir comentarios . En el contenido principal se presenta la imagen del artículo, su referencia y el precio normal, junto con un bloque de compra donde el usuario puede seleccionar la talla y la cantidad antes de añadir el producto al carrito. Además, se incluye un apartado de comentarios con formulario de valoración y listado de reseñas, con opciones de editar y borrar (será solo para administradores).

#### **7. Página de Añadir/Editar Prenda (Administrador)**

![Garment form](images/garment_form.png)


> La página de añadir o editar prenda permite al administrador gestionar el catálogo mediante un formulario . Se incluyen los campos principales de una prenda (nombre, categoría, precio, imagen, descripción y características). Cuando sea añadir estos campos aparecerán vacíos, en cambio, en editar aparecerá la información que estuviese anteriormente. Además, el formulario incorpora validaciones básicas (por ejemplo, rangos de precio y longitudes mínimas de texto) para evitar datos incorrectos . Por último, se incluyen botones de “Guardar” y “Cancelar” para completar o descartar los cambios y se mantiene la navegación y el estilo visual del panel de administración.


#### **8. Página de Carrito / Pedido en progreso**

![Cart1](images/cart1.png)
![Cart2](images/cart2.png)

> La página de carrito permite al usuario revisar los productos añadidos antes de finalizar la compra, mostrando cada artículo con su imagen, nombre, referencia, talla, precio unitario y subtotal. Además, se incluye un control de cantidad mediante input numérico y un botón para eliminar productos. Debajo del listado se añade un apartado de preferencias de entrega donde el usuario puede indicar dirección, fecha preferida y notas de entrega. Finalmente, se presenta un resumen del carrito con el total de productos, el coste de envío y el total a pagar, junto con botones  para continuar comprando o realizar la compra.

#### **9. Página Principal (Administrador)**

![Admin home1](images/admin_home1.png)
![Admin home1](images/admin_home2.png)

La página principal para el administrador mantiene la estructura del catálogo, pero añade opciones orientadas a la gestión de la aplicación. En la barra superior se incorporan accesos directos al carrito y un menú desplegable de perfil, y además aparece un menú específico de administración desde el que se puede navegar a la gestión de usuarios, gestión de pedidos y estadísticas. Se incluye una sección de “Ofertas para ti” al inicio, a modo de apartado de productos destacados. A continuación, se mantiene el listado general de artículos con el mismo sistema de filtrado desplegable (búsqueda, categoría, rango de precio y ordenación). El footer conserva enlaces informativos y redes sociales


#### **10. Página de Gestión de Usuarios (Administrador)**
![All users](images/all_users.png)

> Esta página permite al administrador gestionar los usuarios registrados. La información se presenta en una tabla responsive con los datos principales (ID, nombre, email y si es administrador) y, además, incluye campos adicionales como fecha de alta, número de pedidos y fecha del último pedido. En la parte superior se incorpora un botón para añadir un nuevo usuario y, para cada fila, se incluyen acciones claras de ver, editar y borrar. Cada vez que añadamos un usuario se nos redirigirá a una página similiar a la de registrarse, lo mismo con editar, pero con la información ya añadida. También se añade un botón de “Cargar más” para representar carga dinámica de resultados. La navegación superior específica de administrador (usuarios, pedidos y estadísticas) diferenciandolo del rol de usuario normal.

#### **11. Página de Gestión de Pedidos (Administrador)**

![All orders](images/all_orders.png)

> Esta página permite al administrador gestionar los pedidos realizados . Los pedidos se muestran en una tabla responsive con los datos más importantes (ID del pedido, correo del usuario, fecha y precio total), lo que facilita localizar rápidamente compras concretas. Para cada pedido se incluyen acciones típicas de administración: ver el detalle completo, editar o borrar. Además, en la parte superior se incorpora un botón para añadir nuevos pedidos y, al final de la tabla, un botón de “Cargar más” para representar paginación. La navegación superior se mantiene específica de administrador (usuarios, pedidos y estadísticas).

#### **12. Página de Estadísticas (Administrador)**

![Statistics](images/statistics.png)

> Esta página está pensada para que el administrador pueda consultar el rendimiento de la tienda. En la parte superior se muestran métricas clave en formato de tarjetas (ingresos y número de pedidos) diferenciando distintos periodos, para tener una general sin necesidad de entrar al detalle de cada pedido. Además, se incluyen dos apartados de evolución (ingresos y número de pedidos) representados mediante gráficos, acompañados de un selector de periodo (diario, mensual o anual). La página refleja un panel de control típico de administración, manteniendo la misma navegación superior del rol administrador.

#### **13. Página de Perfil de Usuario**

![User profile](images/user_profile.png)

> La página de perfil permite al usuario consultar y gestionar la información de su cuenta. En la parte superior se muestra el avatar junto con el nombre y el email, y se incluye un botón de “Editar perfil” para acceder directamente a la modificación de datos. A continuación, la información se organiza en dos bloques: por un lado, los datos básicos de la cuenta (nombre, apellidos, email, dirección y fecha de registro) y, por otro, un apartado de actividad con métricas de compra como el ticket medio y el número de compras del último mes. Además, se incorpora una zona de gráfica con selector de periodo (mensual/anual) para representar la evolución del ticket medio. La página mantiene la navegación habitual del usuario registrado (home, carrito y menú de perfil).

#### **14. Página de Mis Pedidos**

![User order](images/user_order.png)

> La página de “Mis pedidos” permite al usuario consultar su historial de compras mediante una tabla con la información principal de cada pedido (ID, fecha y precio total). Para cada pedido se puede acceder al detalle completo del pedido y generar la factura en PDF. Además, se añade un botón de “Cargar más” para representar paginación de pedidos cuando el historial sea más amplio. La página mantiene la navegación habitual del usuario registrado (home, carrito y menú de perfil).

#### **15. Página de Detalle de Pedido / Información de compra**

![Order detail 1](images/order_detail1.png)
![Order detail 2](images/order_detail2.png)

> La página de detalle de pedido permite al usuario/administrador consultar de forma completa la información de una compra concreta. Incluye un botón de “Volver” para regresar al listado de pedidos y muestra un resumen del pedido (fecha, precio total, gastos de envío, total con envío y estado). A continuación, se presenta una tabla con los productos incluidos en el pedido, indicando imagen, talla, cantidad, precio unitario y subtotal, para que el usuario pueda revisar exactamente qué ha comprado. También se añade un apartado con la información de entrega (dirección, fecha preferida y notas), completando el seguimiento del pedido. Por último, se incluye un botón para generar la factura.



## 🛠 **Práctica 1: Web con HTML generado en servidor y AJAX**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube DAW P1 G2](https://www.youtube.com/watch?v=t6XaqsGho6Q)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Navegación y Capturas de Pantalla**

#### **Diagrama de Navegación**

Solo si ha cambiado.

#### **Capturas de Pantalla Actualizadas**

#### **4. Página Principal (usuario registrado)**
![User home 1](images/recomendations.png)
![User home 2](images/load.png)

>La página principal para usuarios registrados mantiene la estructura del catálogo, pero añade funcionalidades pensadas para una experiencia más personalizada. En la barra superior se incorporan accesos directos al carrito y un menú desplegable de perfil, desde el que el usuario puede gestionar sus datos, consultar sus pedidos o cerrar sesión. Se incluye una sección de “Recomendaciones para ti” al inicio, apartado de recomendaciones destacadas. A continuación, se mantiene el listado general de artículos con el mismo sistema de filtrado desplegable (búsqueda, categoría, rango de precio y ordenación), además, tienes la opción de cargar más artículos. El footer conserva enlaces informativos y redes sociales.

### **Instrucciones de Ejecución**

#### **Requisitos Previos**
- **Java**: versión 21 o superior
- **Maven**: versión 3.8 o superior
- **MySQL**: versión 8.0 o superior
- **Git**: para clonar el repositorio
- **Variable de entorno** `DB_PASSWORD` con la contraseña de MySQL

#### **Pasos para ejecutar la aplicación**

1. **Crear el schema en MySQL**
   ```sql
   CREATE SCHEMA IF NOT EXISTS zenda;
   ```

2. **Configurar la variable de entorno `DB_PASSWORD`**
   - Linux/macOS:
     ```bash
     export DB_PASSWORD=tu_contraseña_mysql
     ```
   - Windows (PowerShell):
     ```powershell
     $env:DB_PASSWORD="tu_contraseña_mysql"
     ```

3. **Clonar el repositorio**
   ```bash
   git clone https://github.com/[usuario]/[nombre-repositorio].git
   cd [nombre-repositorio]
   ```

4. **Entrar al backend**
   ```bash
   cd backend
   ```

5 **Compilar el proyecto**
   - Linux/macOS:
     ```bash
     ./mvnw clean install
     ```
   - Windows:
     ```bash
     mvnw.cmd clean install
     ```

6. **Ejecutar la aplicación**
   - Linux/macOS:
     ```bash
     ./mvnw spring-boot:run
     ```
   - Windows:
     ```bash
     mvnw.cmd spring-boot:run
     ```

7. **Abrir en navegador**
   - `https://localhost:8443`

### **Credenciales de prueba**

> En el login se usa **email** como usuario.

- **Administrador**
  - Usuario: `maria@example.com`
  - Contraseña: `password456`

- **Usuario registrado**
  - Usuario: `juan@example.com`
  - Contraseña: `password123`  

### **Diagrama de Entidades de Base de Datos**



![Diagrama Entidad-Relación](images/diagram_BD.png)

> El diagrama entidad-relación de ZENDA Shop muestra las entidades principales del dominio (user_table, garment, order_table, order_item y opinion) y sus relaciones. Un usuario puede realizar varios pedidos y escribir varias opiniones; cada pedido se compone de múltiples líneas en order_item, que enlazan prenda, talla y cantidad, resolviendo la relación N:M entre pedidos y prendas. 
### **Diagrama de Clases y Templates**

Diagrama de clases de la aplicación con diferenciación por colores o secciones:

![Diagrama de Clases](images/template.png)

> La carpeta images almacena todas las capturas de pantalla de la interfaz (para diferentes roles de usuario) y diagramas de la arquitectura (navegación, entidad-relación, clases y componentes React).Los templates HTML ubicados en templates contienen todas las vistas Mustache que genera el servidor: páginas de autenticación (login, registro), perfil de usuario, catálogo de productos, carrito, gestión de pedidos, panel de administración y componentes reutilizables (header, footer, tarjetas de productos); estos templates reciben datos del controlador y aplican estilos CSS desde static.

### **Participación de Miembros en la Práctica 1**

#### **Alumno 1 - [Alejandro Lefebvre Valiente]**

Mis principales responsabilidades y funcionalidades desarrolladas fueron:
Incorporación y primera configuración de la base de datos en el proyecto.
Configuración de la seguridad, los tokens CSRF, los permisos y otras labores relacionadas con los roles.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Incorporación de una base de datos común](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/e7c3a60576b50a625548b6b9b00b9ad33d25108e)  | [application.properties](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/resources/application.properties)   |
|2| [Configuración de seguridad de la web](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/b6c1e030f83b3379a60fdfa27dae9f3a331805fc)  | [WebSecurityConfig](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/security/WebSecurityConfig.java)   |
|3| [Configuración del servicio de detalles de usuario](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/608cbee852a20679af3e897eadc2e9935b695c0c)  | [RepositoryUserDetailsService](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/security/RepositoryUserDetailsService.java)   |
|4| [Configuración del loginController e introducción del sistema de tokens CSRF](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/a80458a3dfc541f061c774eb28919b1514ca8f7d)  | [CSRFHandlerConfiguration](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/security/CSRFHandlerConfiguration.java)   |
|5| [Correcciones en el orderController y configuración de tokens CSRF en formularios](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/494ecd465a302b03d6764518dcf30291042d8a6e)  | [OrderController](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/controller/OrderController.java)   |

---

#### **Alumno 2 - [Víctor Navarro Santos]**

- Desarrollo del controlador de usuarios, implementando la lógica necesaria para su funcionamiento dentro de la aplicación.
- Adaptación y corrección del sistema de permisos existente para permitir el acceso correcto a determinadas funcionalidades.
- Implementación de funcionalidades relacionadas con la gestión de garments.
- Identificación y corrección de errores durante el desarrollo.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Impllementación de la página de Order Management](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/compare/de0dc76aefd210408b732d210dce4a3fc73e196f...6b3add8e75356b3f8fd969938ffeec53f96e8d89)  | [all_orders](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/c7b81863aa1dd6a5f53c323cc61e169a04357fa6/backend/src/main/resources/templates/all_orders.html)   |
|2| [Primer intento de User Controller, sin perfeccionar](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/compare/d6bbae60e846239c3c0729b1bb4506ef5a228644...988069310ee48bfb266c1ec335cfac492ccbc78e)  | [UserController](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/c7b81863aa1dd6a5f53c323cc61e169a04357fa6/backend/src/main/java/es/dawgrupo2/zendashop/controller/UserController.java)   |
|3| [feat: add listing endpoint enhance security configuration (being able to access to "Gestionar usuarios" part being admin)](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/compare/b43ed0877e27fd24a6701cba224e8f3a110ecffa...2077fcf84b37be421a1affd8516c0f84896dd434)  | [User](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/520a3db75996494df0e839ac3afd5f7933fd6b66/backend/src/main/java/es/dawgrupo2/zendashop/model/User.java)   |
|4| [Corrección del código para que el apartado de "gestionar perfil" funcione correctamente](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/compare/2077fcf84b37be421a1affd8516c0f84896dd434...520a3db75996494df0e839ac3afd5f7933fd6b66)  | [User](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/520a3db75996494df0e839ac3afd5f7933fd6b66/backend/src/main/java/es/dawgrupo2/zendashop/model/User.java)   |
|5| [Implementación de la edición de perfiles de usuario por parte del administrador](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/compare/b067ac0fb9a2db67a3f419d3e023d27122ebe93a...c7b81863aa1dd6a5f53c323cc61e169a04357fa6)  | [UserController](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/c7b81863aa1dd6a5f53c323cc61e169a04357fa6/backend/src/main/java/es/dawgrupo2/zendashop/controller/UserController.java)   |

---

#### **Alumno 3 - [Jorge Padilla Rodríguez]**

Creación del html all_users, referido a la estión de los usuarios por parte del administrador y creación de la clase User.

Creación e implementación de la consulta avanzada del proyecto, consistente en recomendar prendas a los usuarios en función a una búsqueda del usuario con más similitud en cuanto a las compras realizadas. Generando a partir de este usuario "gemelo" la elección de prendas compradas por este que aún no haya comprado el usuario principal.

Corrección de errores en el código relacionados con la edición y adición de prendas desde la página web, así como diferentes aspectos del carrito.

Generación del código responsable de la compra desde el carrito por parte del usuario. También responsable del registro efectivo de un nuevo usuario, así como las correciones pertinentes en el html.

Incorporación del controlador de estadísticas y su lógica (sin gráficas).

Adaptación y creación del código necesario para la edición por parte del administrador de pedidos ya realizados.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Consulta avanzada](https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/ce4525a810b85223b0769c814285f7d553b4fd73)  | [GarmentRepository.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/repository/GarmentRepository.java)   |
|2| [Realización de compra desde carrito](https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/f853816b8c1e76454c23c45fdd7a616b539e1500)  | [OrderController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/controller/OrderController.java)   |
|3| [Registrar un nuevo usuario](https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/6cb7b5b85e44c322fd87d040f58318c381bc83be)  | [LoginController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/controller/LoginController.java)   |
|4| [Implementación del controlador de estadísticas](https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/8f49c20b5b748ddcf605928b5b82eacc746f1cfa)  | [StatisticController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/controller/StatisticController.java)   |
|5| [Lógica para editar un pedido realizado](https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/620f0124a60c0f579c658a9c4bb81446b21952cd)  | [order_detail.html](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/resources/templates/order_detail.html)   |

---

#### **Alumno 4 - [Samuel Melián Benito]**

Creación del primer esquema del modelo.

En el frontend, gráficas dinámicas de ingresos y pedidos para la sección de estadísticas, también para la sección de perfil de usuario. 

Migrado de los HTML a plantillas mustache. 

Creación del fichero js para la funcionalidad de cargar más con AJAX.

En el backend, implementación de toda la funcionalidad necesaria para cargar más de todas las entidades, filtros opcionales (y varios simultáneos) para la búsqueda de prendas y combinación del filtro con la funcionalidad de cargar más.

Implementación de la mayoría de los controladores, tratando especialmente detalles como la lógica al deshabilitar una prenda o al deshabilitar un usuario, actualizando carritos con esa prenda.

Control de acceso por dueño en todas las entidades que lo requieren.


| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Migrado de todas los html a plantillas de mustache](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/57636d5b3f1cf9e4de08bebce7c66a2b0420f9e9)  | [GarmentController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/controller/GarmentController.java)   |
|2| [Implementación de filtros (simultáneos y opcionales) para búsqueda de prendas](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/a83999bf44e438f1484439f162d7c782fcfbb6c1)  | [OrderController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/controller/OrderController.java)   |
|3| [Gráficas dinámicas en la sección de estadísticas para ingresos y número de pedidos por distintos perdiodos de tiempo](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/5c39397d683e8d5aa82c18b7692611808ebd5892)  | [OrderService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blame/main/backend/src/main/java/es/dawgrupo2/zendashop/service/OrderService.java)   |
|4| [Implementación de la funcionalidad cargar más para prendas con AJAX](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/a8789fa836443f33e82473f23012def4a72c4b47)  | [User.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blame/main/backend/src/main/java/es/dawgrupo2/zendashop/model/User.java)   |
|5| [Implementación de la funcionalidad de deshabilitar prenda (en sustitución de borrar, para que no se modifiquen los pedidos en los que aparecía). Implementación de la lógica y consultas para actualizar los carritos que contenían dicha prenda](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/f78b5ce418418af00f50194dd650e20c3307ffde)  | [Order.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blame/main/backend/src/main/java/es/dawgrupo2/zendashop/model/Order.java)   |

---

#### **Alumno 5 - Sara Guillén Martínez**

En la parte de usuarios, he mejorado el modelo User añadiendo la unicidad del email, el timestamp de creación y métodos para obtener el nombre de usuario. He trabajado en el proceso de registro y edición, incorporando validaciones de campos y el manejo de errores . También he añadido métodos en UserService para la gestión y guardado de usuarios, incluyendo la gestión de la imagen de perfil.

En cuanto a los pedidos, he implementado los métodos necesarios en OrderService para poder crear, recuperar y gestionar pedidos. He desarrollado la funcionalidad de mostrar todos los pedidos y “mis pedidos”, así como la gestión del carrito (añadir productos y eliminarlos). También he implementado la eliminación de pedidos con control de permisos según el rol del usuario. Además, he añadido la generación de facturas en PDF para los pedidos realizados.

En la gestión de opiniones, he creado y mejorado el OpinionController, implementando la lógica para crear y gestionar opiniones y he añadido la vacidación.

Respecto a las prendas, he refactorizado el formulario de prendas añadiendo validaciones.

Parte del README

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Generar facturas PDF ](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/d32056935c753d851dcfa78f1a6c8671ccf4195b)  | [InvoicePdfService](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blame/main/backend/src/main/java/es/dawgrupo2/zendashop/service/InvoicePdfService.java)  |
|2| [Lógica OpinionController](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/9363a9c9b379af5be8b8cfbd91f8f88c10c6f841)  | [OpinionController](URL_archivo_2)   |
|3| [Borrar un pedido](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/c6b808f27f919460ee2c95959bbc82c24dde9704)  | [OrderController](URL_archivo_3)   |
|4| [Mejorar el registro de usuarios y la gestión de opiniones; añadir validación y manejo de errores](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/d8f8a861147ffa1c04aea050796b2652fc5f9cc5)  | [UserService](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blame/main/backend/src/main/java/es/dawgrupo2/zendashop/service/UserService.java)   |
|5| [Validación del formulario de prendas y el manejo de errores.](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/2890e4b57c7f8c00f556c23cd879975b7bb554a3)  | [GarmentController](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blame/main/backend/src/main/java/es/dawgrupo2/zendashop/controller/GarmentController.java)   |

---

## 🛠 **Práctica 2: Incorporación de una API REST a la aplicación web, despliegue con Docker y despliegue remoto**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://www.youtube.com/watch?v=x91MPoITQ3I)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Documentación de la API REST**

#### **Especificación OpenAPI**
📄 **[Especificación OpenAPI (YAML)](/api-docs/api-docs.yaml)**

#### **Documentación HTML**
📖 **[Documentación API REST (HTML)](https://raw.githack.com/[usuario]/[repositorio]/main/api-docs/api-docs.html)**

> La documentación de la API REST se encuentra en la carpeta `/api-docs` del repositorio. Se ha generado automáticamente con SpringDoc a partir de las anotaciones en el código Java.

### **Diagrama de Clases y Templates Actualizado**

Diagrama actualizado incluyendo los @RestController y su relación con los @Service compartidos:

![Diagrama de Clases Actualizado](images/classes-diagramP2.png)

### **Instrucciones de Ejecución con Docker**

#### **Requisitos previos:**
- Docker instalado (versión 20.10 o superior)
- Docker Compose instalado (versión 2.0 o superior)

#### **Ejecución de la aplicación web**
A. Primera vez (creación de la base de datos)
Si es la primera vez que la arranca y necesita que se generen las tablas
```bash
DOCKERHUB_USER=samuelmelianbenito DDL_AUTO=create docker compose -f oci://samuelmelianbenito/zendashop-compose:0.1.0 up
```
B. Uso habitual
Una vez que ya hayas configurado la primera vez, para futuros arranques (donde ya existan tus datos) sólo necesitas ejecutar esto:
```bash
DOCKERHUB_USER=samuelmelianbenito docker compose -f oci://samuelmelianbenito/zendashop-compose:0.1.0 up
```

#### **Credenciales de Usuarios de Ejemplo**

| Rol | Usuario | Contraseña |
|:---|:---|:---|
| Administrador | maria@example.com | password456 |
| Usuario Registrado | juan@example.com | password123 |
| Usuario Registrado | carlos@example.com | password789 |

### **Participación de Miembros en la Práctica 2**

#### **Alumno 1 - [Alejandro Lefebvre Valiente]**

Mis principales responsabilidades y funcionalidades desarrolladas fueron:
Configuración de la seguridad de la API rest, los JWK tockens, y el RestLoginController para los endpoints de authenticación.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Incorporación de la nueva lógica de seguridad adaptada a la API Rest](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/995c0e4c2534d3c1f2e39530d2a75ed6d52a38a6)  | [SecurityConfig](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/security/SecurityConfig.java)   |
|2| [Controlador Rest del login para con la implementación de endpoints de autenticación](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/627b2173367b66c0ce5af5a296a80116062c706d)  | [RestLoginController](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/controller/auth/LoginRestController.java)   |
|3| [Adaptación e inclusión de la seguridad de la web en la nueva seguridad de la API Rest](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/93bd762aea3ad4871c53eebe3a55b1e0664f3ee5)  | [SecurityConfig](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/security/SecurityConfig.java)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 2 - [Jorge Padilla Rodríguez]**

En esta práctica he llevado a cabo la implementación de la funcionalidad corresponiente a la entidad garment en la API REST, principalmente el desarrollo del GarmentRestController. Además, he implementado la tecnología de generación de facturas en pdf de un pedido. Posteriormente, me he encargado de realizar la comprobación y depuración de la totalidad del código y su funcionalidad, así como la generación de la colección postman durante el proceso, para su comprobación, corrigiendo distintos aspectos más o menos relevantes del código para la correcta implmentación final de la API REST. Se destaca también en este último punto la implementación del control por rol y dueño de imágenes, entre otros.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Primera aproximación de la implementación del GarmentRestController](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/7e180abd97a028f08c2828b3108f0927702e5d65)  | [GarmentRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/controller/GarmentRestController.java)   |
|2| [Implementación de la tecnología de generación de facturas en pdf](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/f5f221196731a6573bbe9c3af2c6fbd4eb029297)  | [OrderRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/controller/OrderRestController.java)   |
|3| [Realización de la colección postman, tras un proceso de depuración simultáneo](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/b0a71df470166709c7d5492075b9197f32f35347)  | [api.postman_collection.json](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/api.postman_collection.json)   |
|4| [Final de la implementación del GarmentRestController](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/ea5422e2ab441e3a37e02652d92197348b8f33e3)  | [GarmentRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/controller/GarmentRestController.java)   |
|5| [Ejemplo de uno de los procesos de revisión en los que se implementa el control por rol y dueño de imágenes de usuario](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/9a61f9afa212bc19e04d0fc68314590b3c119d3d)  | [ImageRestController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/controller/ImageRestController.java)   |

---

#### **Alumno 3 - [Víctor Navarro Santos]**

Mis principales responsabilidades en esta práctica fueron:
El desarrollo, seguridad, y optimización de la API REST de gestión de usuarios, además de la estructuración y generación de la documentación de endpoints (api-doc)

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Construcción de la base del Controlador Rest de usuarios](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/443ef9c771e375d4421b51c7b96d860ee3cc5c2d)  | [UserRestController](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/src/main/java/es/dawgrupo2/zendashop/controller/UserRestController.java)   |
|2| [Adición del endpoint de registro de usuarios y manejo de errores](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/cd78cf8655a09952addddbd20616a402ef69c7af)  | [Archivo2](URL_archivo_2)   |
|3| [Conexión con la la lógica de negocio (pedidos)](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/aba41ce60bda675de6ba69b96ad9fa27293e3231)  | [pom.xml](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/backend/pom.xml)   |
|4| [Generación del archivo .yaml para la documentación de OpenAPI](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/0ca2cf92db7c2a7af422e9abe8bb96e92d0ead09)  | [api-docs.yaml](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/api-docs/api-docs.yaml)   |
|5| [Generación del archivo .html para la documentación de OpenApi](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/commit/76e97d4b7f9c203e916a744d6869182c9fec6617)  | [api-docs.html](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-2/blob/main/api-docs/api-docs.html)   |

---

#### **Alumno 3 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

## 🛠 **Práctica 3: Implementación de la web con arquitectura SPA**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](URL_del_video)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Preparación del Entorno de Desarrollo**

#### **Requisitos Previos**
- **Node.js**: versión 18.x o superior
- **npm**: versión 9.x o superior (se instala con Node.js)
- **Git**: para clonar el repositorio

#### **Pasos para configurar el entorno de desarrollo**

1. **Instalar Node.js y npm**
   
   Descarga e instala Node.js desde [https://nodejs.org/](https://nodejs.org/)
   
   Verifica la instalación:
   ```bash
   node --version
   npm --version
   ```

2. **Clonar el repositorio** (si no lo has hecho ya)
   ```bash
   git clone https://github.com/[usuario]/[nombre-repositorio].git
   cd [nombre-repositorio]
   ```

3. **Navegar a la carpeta del proyecto React**
   ```bash
   cd frontend
   ```

4. **AQUÍ LOS SIGUIENTES PASOS**

### **Diagrama de Clases y Templates de la SPA**

Diagrama mostrando los componentes React, hooks personalizados, servicios y sus relaciones:

![Diagrama de Componentes React](images/spa-classes-diagram.png)

### **Participación de Miembros en la Práctica 3**

#### **Alumno 1 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 2 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 3 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 4 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

