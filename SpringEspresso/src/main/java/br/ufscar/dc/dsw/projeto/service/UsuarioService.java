package br.ufscar.dc.dsw.projeto.service;

import br.ufscar.dc.dsw.projeto.model.UsuarioModel;
import br.ufscar.dc.dsw.projeto.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = false)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioModel> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public UsuarioModel buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public UsuarioModel buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public UsuarioModel salvar(UsuarioModel usuario) {

        if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        return usuarioRepository.save(usuario);
    }

    public UsuarioModel atualizar(UsuarioModel usuario) {
        UsuarioModel usuarioExistente = buscarPorId(usuario.getId());
        if (usuarioExistente != null) {
            usuarioExistente.setNome(usuario.getNome());
            usuarioExistente.setEmail(usuario.getEmail());
            usuarioExistente.setRole(usuario.getRole());
            
            // SE FOR FORNECIDA, ATUALIZAR A SENHA
            if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
                usuarioExistente.setSenha(passwordEncoder.encode(usuario.getSenha()));
            }
            
            return usuarioRepository.save(usuarioExistente);
        }
        return null;
    }

    @Transactional
    public void remover(Long id) {
        UsuarioModel usuario = buscarPorId(id);
        if (usuario != null) {
            usuarioRepository.delete(usuario);
        }
    }

    @Transactional(readOnly = true)
    public boolean emailJaExiste(String email, Long idUsuario) {
        UsuarioModel usuario = usuarioRepository.findByEmail(email);
        return usuario != null && !usuario.getId().equals(idUsuario);
    }

    @Transactional(readOnly = true)
    public boolean emailJaExiste(String email) {
        return usuarioRepository.findByEmail(email) != null;
    }

    @Transactional(readOnly = true)
    public UsuarioModel getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UsuarioModel) {
                return (UsuarioModel) principal;
            }
        }
        return null;
    }
}
