package com.nzaeta.cloudinary.repository;

import com.nzaeta.cloudinary.entity.Imagen;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {
    List<Imagen> findByOrderById();
}
