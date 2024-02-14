package com.nzaeta.cloudinary.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nzaeta.cloudinary.entity.Imagen;
import com.nzaeta.cloudinary.service.CloudinaryService;
import com.nzaeta.cloudinary.service.ImagenService;

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