package org.udv.nrc.caixaudv.web.rest;
import org.udv.nrc.caixaudv.domain.Conta;
import org.udv.nrc.caixaudv.domain.Ressarcimento;
import org.udv.nrc.caixaudv.domain.enumeration.NivelPermissao;
import org.udv.nrc.caixaudv.repository.ContaRepository;
import org.udv.nrc.caixaudv.repository.RessarcimentoRepository;
import org.udv.nrc.caixaudv.security.UserAccountPermissionChecker;
import org.udv.nrc.caixaudv.web.rest.errors.BadRequestAlertException;
import org.udv.nrc.caixaudv.web.rest.errors.UserNotAuthorizedException;
import org.udv.nrc.caixaudv.web.rest.util.HeaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Ressarcimento.
 */
@RestController
@RequestMapping("/api")
public class RessarcimentoResource {

    private final Logger log = LoggerFactory.getLogger(RessarcimentoResource.class);

    private static final String ENTITY_NAME = "ressarcimento";

    private final RessarcimentoRepository ressarcimentoRepository;

    @Autowired
    private ContaRepository contaRepository;

    private final List<NivelPermissao> canCRAll = Arrays.asList(NivelPermissao.ADMIN, 
        NivelPermissao.OPERADOR);

    public RessarcimentoResource(RessarcimentoRepository ressarcimentoRepository) {
        this.ressarcimentoRepository = ressarcimentoRepository;
    }

    /**
     * POST  /ressarcimentos : Create a new ressarcimento.
     *
     * @param ressarcimento the ressarcimento to create
     * @return the ResponseEntity with status 201 (Created) and with body the new ressarcimento, or with status 400 (Bad Request) if the ressarcimento has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/ressarcimentos")
    public ResponseEntity<Ressarcimento> createRessarcimento(@Valid @RequestBody Ressarcimento ressarcimento) throws URISyntaxException {
        log.debug("REST request to save Ressarcimento : {}", ressarcimento);
        if (ressarcimento.getId() != null) {
            throw new BadRequestAlertException("A new ressarcimento cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Conta currentConta = contaRepository.findByUserIsCurrentUser();
        if(!UserAccountPermissionChecker.checkPermissao(currentConta, canCRAll)){
            throw new UserNotAuthorizedException("Usuário não autorizado!", ENTITY_NAME, "missing_permission");
        }
        Ressarcimento result = ressarcimentoRepository.save(ressarcimento);
        return ResponseEntity.created(new URI("/api/ressarcimentos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /ressarcimentos : Updates an existing ressarcimento.
     *
     * @param ressarcimento the ressarcimento to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated ressarcimento,
     * or with status 400 (Bad Request) if the ressarcimento is not valid,
     * or with status 500 (Internal Server Error) if the ressarcimento couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/ressarcimentos")
    public ResponseEntity<Ressarcimento> updateRessarcimento(@Valid @RequestBody Ressarcimento ressarcimento) throws URISyntaxException {
        log.debug("REST request to update Ressarcimento : {}", ressarcimento);
        if (ressarcimento.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Conta currentConta = contaRepository.findByUserIsCurrentUser();
        if(!currentConta.getNivelPermissao().equals(NivelPermissao.ADMIN)){
            throw new UserNotAuthorizedException("Usuário não autorizado!", ENTITY_NAME, "missing_permission");
        }
        Ressarcimento result = ressarcimentoRepository.save(ressarcimento);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, ressarcimento.getId().toString()))
            .body(result);
    }

    /**
     * GET  /ressarcimentos : get all the ressarcimentos.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of ressarcimentos in body
     */
    @GetMapping("/ressarcimentos")
    public List<Ressarcimento> getAllRessarcimentos() {
        log.debug("REST request to get all Ressarcimentos");
        Conta currentConta = contaRepository.findByUserIsCurrentUser();
        if(!UserAccountPermissionChecker.checkPermissao(currentConta, canCRAll)){
            ressarcimentoRepository.findAll().stream()
                .filter(ressarcimento -> ressarcimento.getConta().equals(currentConta));
        }
        return ressarcimentoRepository.findAll();
    }

    /**
     * GET  /ressarcimentos/:id : get the "id" ressarcimento.
     *
     * @param id the id of the ressarcimento to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the ressarcimento, or with status 404 (Not Found)
     */
    @GetMapping("/ressarcimentos/{id}")
    public ResponseEntity<Ressarcimento> getRessarcimento(@PathVariable Long id) {
        log.debug("REST request to get Ressarcimento : {}", id);
        Optional<Ressarcimento> ressarcimento = ressarcimentoRepository.findById(id);
        Conta currentConta = contaRepository.findByUserIsCurrentUser();
        if(ressarcimento.isPresent()){
            if(ressarcimento.get().getConta().equals(currentConta) || 
                    currentConta.getNivelPermissao().equals(NivelPermissao.ADMIN) ||
                    currentConta.getNivelPermissao().equals(NivelPermissao.OPERADOR))
                return ResponseEntity.ok(ressarcimento.get());
            else throw new UserNotAuthorizedException("Usuário não autorizado!", ENTITY_NAME, "missing_permission");
        }
        else return ResponseEntity.notFound().build();
    }

    /**
     * DELETE  /ressarcimentos/:id : delete the "id" ressarcimento.
     *
     * @param id the id of the ressarcimento to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/ressarcimentos/{id}")
    public ResponseEntity<Void> deleteRessarcimento(@PathVariable Long id) {
        log.debug("REST request to delete Ressarcimento : {}", id);
        Conta currentConta = contaRepository.findByUserIsCurrentUser();
        if(!currentConta.getNivelPermissao().equals(NivelPermissao.ADMIN)) {
            throw new UserNotAuthorizedException("Usuário não autorizado!", ENTITY_NAME, "missing_permission");
        }
        ressarcimentoRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
