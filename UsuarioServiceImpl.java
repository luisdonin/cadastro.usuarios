package com.br.td.utfpr.edu.tsi.cadastro_usuarios.service;

import com.br.td.utfpr.edu.tsi.cadastro_usuarios.model.Usuario;
import com.br.td.utfpr.edu.tsi.cadastro_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public void cadastrar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo.");
        }
        if (usuario.getIdade() == null) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória.");
        }

        if(usuario.getEmail() == null){
            throw new IllegalArgumentException("Email é obrigatório.");
        }

        int idade = Period.between(usuario.getIdade(), LocalDate.now()).getYears();
        if (idade < 18) {
            throw new IllegalArgumentException("Apenas usuários com 18 anos ou mais podem ser cadastrados.");
        }

        if (usuario.getId() == null || usuario.getId().isEmpty()) {
            usuario.setId(UUID.randomUUID().toString());
        }

        usuarioRepository.save(usuario);
    }


    @Override
    public Usuario localizar(String idUsuario) {
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);
        if (usuario.isPresent()) {
            return usuario.get();
        }
        return null;
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public void atualizar(Usuario usuario) {
        if (usuario == null || usuario.getId() == null || usuario.getId().isEmpty()) {
            throw new IllegalArgumentException("ID do usuário é obrigatório para atualização.");
        }

        Optional<Usuario> existenteOpt = usuarioRepository.findById(usuario.getId());
        if (!existenteOpt.isPresent()) {
            throw new IllegalArgumentException("Usuário não encontrado: " + usuario.getId());
        }

        Usuario existente = existenteOpt.get();

        // Atualiza campos permitidos
        existente.setNome(usuario.getNome());
        existente.setEmail(usuario.getEmail());

        if (usuario.getIdade() != null) {
            int idade = Period.between((LocalDate) usuario.getIdade(), LocalDate.now()).getYears();
            if (idade < 18) {
                throw new IllegalArgumentException("Apenas usuários com 18 anos ou mais podem ser cadastrados.");
            }
            existente.setIdade(usuario.getIdade());
        }

        usuarioRepository.save(existente);
    }

    @Override
    public void remover(String idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }


}
