package com.raze.cancha.controller;
import com.raze.cancha.catalog.Rol;
import com.raze.cancha.domain.Empresa;
import com.raze.cancha.domain.Usuario;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.roo.addon.web.mvc.controller.finder.RooWebFinder;

@RooWebJson(jsonObject = Usuario.class)
@Controller
@RequestMapping("/usuarios")
@RooWebScaffold(path = "usuarios", formBackingObject = Usuario.class)
@RooWebFinder
public class UsuarioController {

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid Usuario usuario, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, usuario);
            return "usuarios/create";
        }
        uiModel.asMap().clear();
        usuario.persist();
        return "redirect:/usuarios/" + encodeUrlPathSegment(usuario.getId().toString(), httpServletRequest);
    }

	@RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Usuario());
        List<String[]> dependencies = new ArrayList<String[]>();
        if (Empresa.countEmpresas() == 0) {
            dependencies.add(new String[] { "empresa", "empresas" });
        }
        if (Rol.countRols() == 0) {
            dependencies.add(new String[] { "rol", "rols" });
        }
        uiModel.addAttribute("dependencies", dependencies);
        return "usuarios/create";
    }

	@RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("usuario", Usuario.findUsuario(id));
        uiModel.addAttribute("itemId", id);
        return "usuarios/show";
    }

	@RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("usuarios", Usuario.findUsuarioEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) Usuario.countUsuarios() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("usuarios", Usuario.findAllUsuarios(sortFieldName, sortOrder));
        }
        addDateTimeFormatPatterns(uiModel);
        return "usuarios/list";
    }

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid Usuario usuario, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, usuario);
            return "usuarios/update";
        }
        uiModel.asMap().clear();
        usuario.merge();
        return "redirect:/usuarios/" + encodeUrlPathSegment(usuario.getId().toString(), httpServletRequest);
    }

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, Usuario.findUsuario(id));
        return "usuarios/update";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Usuario usuario = Usuario.findUsuario(id);
        usuario.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/usuarios";
    }

	void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("usuario_fechanacimiento_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
        uiModel.addAttribute("usuario_fechacreacion_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
        uiModel.addAttribute("usuario_fechamodificacion_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
    }

	void populateEditForm(Model uiModel, Usuario usuario) {
        uiModel.addAttribute("usuario", usuario);
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("rols", Rol.findAllRols());
        uiModel.addAttribute("empresas", Empresa.findAllEmpresas());
        uiModel.addAttribute("usuarios", Usuario.findAllUsuarios());
    }

	String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }

	@RequestMapping(params = { "find=ByApellidoPaternoLike", "form" }, method = RequestMethod.GET)
    public String findUsuariosByApellidoPaternoLikeForm(Model uiModel) {
        return "usuarios/findUsuariosByApellidoPaternoLike";
    }

	@RequestMapping(params = "find=ByApellidoPaternoLike", method = RequestMethod.GET)
    public String findUsuariosByApellidoPaternoLike(@RequestParam("apellidoPaterno") String apellidoPaterno, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("usuarios", Usuario.findUsuariosByApellidoPaternoLike(apellidoPaterno, sortFieldName, sortOrder).setFirstResult(firstResult).setMaxResults(sizeNo).getResultList());
            float nrOfPages = (float) Usuario.countFindUsuariosByApellidoPaternoLike(apellidoPaterno) / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("usuarios", Usuario.findUsuariosByApellidoPaternoLike(apellidoPaterno, sortFieldName, sortOrder).getResultList());
        }
        addDateTimeFormatPatterns(uiModel);
        return "usuarios/list";
    }

	@RequestMapping(params = { "find=ByEmpresaAndActivo", "form" }, method = RequestMethod.GET)
    public String findUsuariosByEmpresaAndActivoForm(Model uiModel) {
        uiModel.addAttribute("empresas", Empresa.findAllEmpresas());
        return "usuarios/findUsuariosByEmpresaAndActivo";
    }

	@RequestMapping(params = "find=ByEmpresaAndActivo", method = RequestMethod.GET)
    public String findUsuariosByEmpresaAndActivo(@RequestParam("empresa") Empresa empresa, @RequestParam(value = "activo", required = false) Boolean activo, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("usuarios", Usuario.findUsuariosByEmpresaAndActivo(empresa, activo == null ? Boolean.FALSE : activo, sortFieldName, sortOrder).setFirstResult(firstResult).setMaxResults(sizeNo).getResultList());
            float nrOfPages = (float) Usuario.countFindUsuariosByEmpresaAndActivo(empresa, activo == null ? Boolean.FALSE : activo) / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("usuarios", Usuario.findUsuariosByEmpresaAndActivo(empresa, activo == null ? Boolean.FALSE : activo, sortFieldName, sortOrder).getResultList());
        }
        addDateTimeFormatPatterns(uiModel);
        return "usuarios/list";
    }

	@RequestMapping(params = { "find=ByEmpresaAndNombreLikeAndActivo", "form" }, method = RequestMethod.GET)
    public String findUsuariosByEmpresaAndNombreLikeAndActivoForm(Model uiModel) {
        uiModel.addAttribute("empresas", Empresa.findAllEmpresas());
        return "usuarios/findUsuariosByEmpresaAndNombreLikeAndActivo";
    }

	@RequestMapping(params = "find=ByEmpresaAndNombreLikeAndActivo", method = RequestMethod.GET)
    public String findUsuariosByEmpresaAndNombreLikeAndActivo(@RequestParam("empresa") Empresa empresa, @RequestParam("nombre") String nombre, @RequestParam(value = "activo", required = false) Boolean activo, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("usuarios", Usuario.findUsuariosByEmpresaAndNombreLikeAndActivo(empresa, nombre, activo == null ? Boolean.FALSE : activo, sortFieldName, sortOrder).setFirstResult(firstResult).setMaxResults(sizeNo).getResultList());
            float nrOfPages = (float) Usuario.countFindUsuariosByEmpresaAndNombreLikeAndActivo(empresa, nombre, activo == null ? Boolean.FALSE : activo) / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("usuarios", Usuario.findUsuariosByEmpresaAndNombreLikeAndActivo(empresa, nombre, activo == null ? Boolean.FALSE : activo, sortFieldName, sortOrder).getResultList());
        }
        addDateTimeFormatPatterns(uiModel);
        return "usuarios/list";
    }

	@RequestMapping(params = { "find=ByEmpresaCorreoELikeAndActivo", "form" }, method = RequestMethod.GET)
    public String findUsuariosByEmpresaCorreoELikeAndActivoForm(Model uiModel) {
        uiModel.addAttribute("empresas", Empresa.findAllEmpresas());
        return "usuarios/findUsuariosByEmpresaCorreoELikeAndActivo";
    }

	@RequestMapping(params = "find=ByEmpresaCorreoELikeAndActivo", method = RequestMethod.GET)
    public String findUsuariosByEmpresaCorreoELikeAndActivo(@RequestParam("empresa") Empresa empresa, @RequestParam("correoE") String correoE, @RequestParam(value = "activo", required = false) Boolean activo, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("usuarios", Usuario.findUsuariosByEmpresaCorreoELikeAndActivo(empresa, correoE, activo == null ? Boolean.FALSE : activo, sortFieldName, sortOrder).setFirstResult(firstResult).setMaxResults(sizeNo).getResultList());
            float nrOfPages = (float) Usuario.countFindUsuariosByEmpresaCorreoELikeAndActivo(empresa, correoE, activo == null ? Boolean.FALSE : activo) / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("usuarios", Usuario.findUsuariosByEmpresaCorreoELikeAndActivo(empresa, correoE, activo == null ? Boolean.FALSE : activo, sortFieldName, sortOrder).getResultList());
        }
        addDateTimeFormatPatterns(uiModel);
        return "usuarios/list";
    }

	@RequestMapping(params = { "find=ByRolAndActivo", "form" }, method = RequestMethod.GET)
    public String findUsuariosByRolAndActivoForm(Model uiModel) {
        uiModel.addAttribute("rols", Rol.findAllRols());
        return "usuarios/findUsuariosByRolAndActivo";
    }

	@RequestMapping(params = "find=ByRolAndActivo", method = RequestMethod.GET)
    public String findUsuariosByRolAndActivo(@RequestParam("rol") Rol rol, @RequestParam(value = "activo", required = false) Boolean activo, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("usuarios", Usuario.findUsuariosByRolAndActivo(rol, activo == null ? Boolean.FALSE : activo, sortFieldName, sortOrder).setFirstResult(firstResult).setMaxResults(sizeNo).getResultList());
            float nrOfPages = (float) Usuario.countFindUsuariosByRolAndActivo(rol, activo == null ? Boolean.FALSE : activo) / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("usuarios", Usuario.findUsuariosByRolAndActivo(rol, activo == null ? Boolean.FALSE : activo, sortFieldName, sortOrder).getResultList());
        }
        addDateTimeFormatPatterns(uiModel);
        return "usuarios/list";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        Usuario usuario = Usuario.findUsuario(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (usuario == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(usuario.toJson(), headers, HttpStatus.OK);
    }

	@RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<Usuario> result = Usuario.findAllUsuarios();
        return new ResponseEntity<String>(Usuario.toJsonArray(result), headers, HttpStatus.OK);
    }

	@RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        Usuario usuario = Usuario.fromJsonToUsuario(json);
        usuario.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RequestMapping a = (RequestMapping) getClass().getAnnotation(RequestMapping.class);
        headers.add("Location",uriBuilder.path(a.value()[0]+"/"+usuario.getId().toString()).build().toUriString());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (Usuario usuario: Usuario.fromJsonArrayToUsuarios(json)) {
            usuario.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        Usuario usuario = Usuario.fromJsonToUsuario(json);
        usuario.setId(id);
        if (usuario.merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        Usuario usuario = Usuario.findUsuario(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (usuario == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        usuario.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(params = "find=ByApellidoPaternoLike", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindUsuariosByApellidoPaternoLike(@RequestParam("apellidoPaterno") String apellidoPaterno) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(Usuario.toJsonArray(Usuario.findUsuariosByApellidoPaternoLike(apellidoPaterno).getResultList()), headers, HttpStatus.OK);
    }

	@RequestMapping(params = "find=ByEmpresaAndActivo", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindUsuariosByEmpresaAndActivo(@RequestParam("empresa") Empresa empresa, @RequestParam(value = "activo", required = false) Boolean activo) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(Usuario.toJsonArray(Usuario.findUsuariosByEmpresaAndActivo(empresa, activo == null ? Boolean.FALSE : activo).getResultList()), headers, HttpStatus.OK);
    }

	@RequestMapping(params = "find=ByEmpresaAndNombreLikeAndActivo", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindUsuariosByEmpresaAndNombreLikeAndActivo(@RequestParam("empresa") Empresa empresa, @RequestParam("nombre") String nombre, @RequestParam(value = "activo", required = false) Boolean activo) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(Usuario.toJsonArray(Usuario.findUsuariosByEmpresaAndNombreLikeAndActivo(empresa, nombre, activo == null ? Boolean.FALSE : activo).getResultList()), headers, HttpStatus.OK);
    }

	@RequestMapping(params = "find=ByEmpresaCorreoELikeAndActivo", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindUsuariosByEmpresaCorreoELikeAndActivo(@RequestParam("empresa") Empresa empresa, @RequestParam("correoE") String correoE, @RequestParam(value = "activo", required = false) Boolean activo) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(Usuario.toJsonArray(Usuario.findUsuariosByEmpresaCorreoELikeAndActivo(empresa, correoE, activo == null ? Boolean.FALSE : activo).getResultList()), headers, HttpStatus.OK);
    }

	@RequestMapping(params = "find=ByRolAndActivo", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindUsuariosByRolAndActivo(@RequestParam("rol") Rol rol, @RequestParam(value = "activo", required = false) Boolean activo) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(Usuario.toJsonArray(Usuario.findUsuariosByRolAndActivo(rol, activo == null ? Boolean.FALSE : activo).getResultList()), headers, HttpStatus.OK);
    }
}
