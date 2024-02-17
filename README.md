# Tutorial de manejo de imágenes con Cloudinary en Java

## 1 - Crear cuenta en Cloudinary

Ir a la web de [**Cloudinary**](https://cloudinary.com/users/register_free "ir al sitio web de Cloudinary") y registrarse o iniciar sesión con cuenta de Google.<br>
En el DASHBOARD se pueden ver los 3 datos que serán necesarios para vincular esta cuenta de Cloudinary con tu proyecto en Java.

![image](https://github.com/nzaeta/Cloudinary/assets/106348660/424321ad-b3ef-4917-bce7-e89e45c5d263)


En MEDIA EXPLORER aparecerán las imágenes que carguemos. Para empezar habrá unas imágenes de muestra (samples)

![image](https://github.com/nzaeta/Cloudinary/assets/106348660/447e0f73-a548-43cc-af22-038fcabed915)




## 2 - Agregar dependencia a pom.xml

```html
<dependency>
	<groupId>com.cloudinary</groupId>
	<artifactId>cloudinary-http44</artifactId>
	<version>1.32.2</version>
</dependency>
```
## 3 - Crear clase servicio CloudinaryService

Esta clase contiene los métodos para cargar y eliminar las imágenes de Cloudinary.<br>
COMPLETAR los valores de cloud_name, api_key y api_secret con los valores correspondientes que figuran en el DASHBOARD.<br>
Definir un nombre para la carpeta en donde se guardarán las imágenes.

```java
@Service
public class CloudinaryService {

    Cloudinary cloudinary;

    private Map<String, String> valuesMap = new HashMap<>();

    public CloudinaryService() {
        valuesMap.put("cloud_name", "COMPLETAR");
        valuesMap.put("api_key", "COMPLETAR");
        valuesMap.put("api_secret", "COMPLETAR");
        cloudinary = new Cloudinary(valuesMap);   
    }
    

    public Map upload(MultipartFile multipartFile) throws IOException {
        File file = convert(multipartFile);
        Map result = cloudinary.uploader().upload(file, ObjectUtils.asMap("folder", "NOMBRECARPETA/"));
        file.delete();
        return result;
    }

    public Map delete(String id) throws IOException {
        Map result = cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
        return result;
    }

    private File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(multipartFile.getBytes());
        fo.close();
        return file;
    }
}
```

## 4 - Crear clase entidad Imagen

Estos serán los atributos de la entidad Imagen:
- Id autogenerado de cada registro en tu Base de Datos
- nombre del archivo original
- Url de la imagen
- Id asignado en el drive de Cloudinary



```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Imagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String imagenUrl;
    private String cloudinaryId;
}
```

## 4 - Crear interfaz repositorio ImagenRepository

```java
@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {
    List<Imagen> findByOrderById();
}
```

## 5 - Crear clase servicio ImagenService

```java
@Service
@Transactional
public class ImagenService {

    @Autowired
    ImagenRepository imagenRepository;

    public Optional<Imagen> getImagen(Long id){
        return imagenRepository.findById(id);
    }

    public void save(Imagen imagen){
        imagenRepository.save(imagen);
    }

    public void delete(Long id){
        imagenRepository.deleteById(id);
    }

    public boolean exists(Long id){
        return imagenRepository.existsById(id);
    }
    
    public List<Imagen> list(){
        return imagenRepository.findByOrderById();
    }
}

```

## 6 - Crear clase controlador ImagenController

Aquí tendremos los endpoints para:
- Listar todas las imágenes
- Buscar una imagen por su Id (el autogenerado en la Base de Datos, no el HASH generado por Cloudinary)
- Subir una imagen
- Eliminar una imagen por su Id (se borrará tanto en la base de datos como en el drive de Cloudinary).


```java
@RestController
@RequestMapping("/imagen")
public class ImagenController {

	@Autowired
	CloudinaryService cloudinaryService;

	@Autowired
	ImagenService imagenService;

	@GetMapping("/list")
	public ResponseEntity<List<Imagen>> list() {
		List<Imagen> list = imagenService.list();
		return new ResponseEntity(list, HttpStatus.OK);
	}

	@GetMapping("/{fileId}")
	public ResponseEntity<?> getImagen(@PathVariable Long fileId) throws IOException {
		Imagen imageData = imagenService.getImagen(fileId).get();
		return ResponseEntity.status(HttpStatus.OK).body(imageData);
	}

	@PostMapping("/upload")
	public ResponseEntity<?> upload(@RequestParam("imagen") MultipartFile multipartFile) throws IOException {
		BufferedImage bi = ImageIO.read(multipartFile.getInputStream());
		if (bi == null) {
			return new ResponseEntity("imagen no válida", HttpStatus.BAD_REQUEST);
		}
		Map result = cloudinaryService.upload(multipartFile);
		Imagen imagen = new Imagen();
		imagen.setName((String) result.get("original_filename"));
		imagen.setImagenUrl((String) result.get("url"));
		imagen.setCloudinaryId((String) result.get("public_id"));

		imagenService.save(imagen);
		return new ResponseEntity(imagen, HttpStatus.OK);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id) throws IOException {
		if (!imagenService.exists(id))
			return new ResponseEntity("no existe", HttpStatus.NOT_FOUND);
		Imagen imagen = imagenService.getImagen(id).get();
		Map result = cloudinaryService.delete(imagen.getCloudinaryId());
		imagenService.delete(id);
		return new ResponseEntity("imagen eliminada", HttpStatus.OK);
	}
}
```

## 7 - Testear endpoints en Postman

Para subir una imagen (método POST "upload") elegir Body --> form-data.<br>
En el campo "Key" escribir "imagen". Elegir la opción "File" en el desplegable de ese campo. Luego en "Value" seleccionar el archivo a subir.

![image](https://github.com/nzaeta/Cloudinary/assets/106348660/68f36b27-3638-43cf-b5df-5ca4e4460eb5)


<br>

## PARA EJECUTAR EL PROYECTO DE ESTE REPOSITORIO:

- Crear una base de datos de nombre "cloudinary".
- Revisar en application.properties las credenciales de acceso (por defecto username: root, password: root)
- Completa los valores de cloud_name, api_key y api_secret y el nombre de la carpeta para guardar las imágenes. (VER PASO #3)

<br>
















