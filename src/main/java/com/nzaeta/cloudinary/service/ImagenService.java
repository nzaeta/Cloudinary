package com.nzaeta.cloudinary.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nzaeta.cloudinary.entity.Imagen;
import com.nzaeta.cloudinary.repository.ImagenRepository;


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