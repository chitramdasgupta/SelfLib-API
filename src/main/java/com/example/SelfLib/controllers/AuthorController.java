package com.example.SelfLib.controllers;

import com.example.SelfLib.domain.dto.AuthorDto;
import com.example.SelfLib.domain.entities.AuthorEntity;
import com.example.SelfLib.mappers.EntityDtoMapper;
import com.example.SelfLib.services.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("authors")
public class AuthorController {
    private final AuthorService authorService;
    private final EntityDtoMapper entityDtoMapper;

    public AuthorController(AuthorService authorService, EntityDtoMapper entityDtoMapper) {
        this.authorService = authorService;
        this.entityDtoMapper = entityDtoMapper;
    }

    @GetMapping("/")
    public List<AuthorDto> listAuthors() {
        List<AuthorEntity> authors = authorService.findAll();
        return authors.stream().map(entityDtoMapper::authorEntityToDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthor(@PathVariable("id") Long id) {
        Optional<AuthorEntity> foundAuthor = authorService.findOne(id);

        return foundAuthor.map(authorEntity -> {
            AuthorDto authorDto = entityDtoMapper.authorEntityToDto(authorEntity);
            return new ResponseEntity<>(authorDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/")
    public ResponseEntity<AuthorDto> createAuthor(@Valid @RequestBody AuthorDto authorDto) {
        AuthorEntity authorEntity = entityDtoMapper.authorDtoToEntity(authorDto);
        AuthorEntity savedAuthorEntity = authorService.save(authorEntity);
        return new ResponseEntity<>(entityDtoMapper.authorEntityToDto(savedAuthorEntity), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> fullUpdateAuthor(@PathVariable("id") Long id, @Valid @RequestBody AuthorDto authorDto) {
        Optional<AuthorEntity> foundAuthor = authorService.findOne(id);

        if (foundAuthor.isPresent()) {
            AuthorEntity authorEntity = entityDtoMapper.authorDtoToEntity(authorDto);
            AuthorEntity updatedAuthorEntity = authorService.save(authorEntity);

            return new ResponseEntity<>(entityDtoMapper.authorEntityToDto(updatedAuthorEntity), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PatchMapping("/{id}")
    public ResponseEntity<AuthorDto> partialUpdateAuthor(@PathVariable("id") Long id, @Valid @RequestBody AuthorDto authorDto) {
        Optional<AuthorEntity> foundAuthor = authorService.findOne(id);

        if (foundAuthor.isPresent()) {
            AuthorEntity authorEntity = entityDtoMapper.authorDtoToEntity(authorDto);
            authorEntity.setId(id);
            AuthorEntity updatedAuthorEntity = authorService.partialUpdate(authorEntity);

            return new ResponseEntity<>(entityDtoMapper.authorEntityToDto(updatedAuthorEntity), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable("id") Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
