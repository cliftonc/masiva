package org.damongo

class DamObjectController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	
    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [damObjectInstanceList: DamObject.list(params), damObjectInstanceTotal: DamObject.count()]
    }
		
    def create = {
        def damObjectInstance = new DamObject()
        damObjectInstance.properties = params
        return [damObjectInstance: damObjectInstance]
    }

    def save = {
        def damObjectInstance = new DamObject(params)
        if (damObjectInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'damObject.label', default: 'DamObject'), damObjectInstance.id])}"
            redirect(action: "show", id: damObjectInstance.id)
        }
        else {
            render(view: "create", model: [damObjectInstance: damObjectInstance])
        }
    }

    def show = {
        def damObjectInstance = DamObject.get(params.id)
        if (!damObjectInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'damObject.label', default: 'DamObject'), params.id])}"
            redirect(action: "list")
        }
        else {
            [damObjectInstance: damObjectInstance]
        }
    }

    def edit = {
        def damObjectInstance = DamObject.get(params.id)
        if (!damObjectInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'damObject.label', default: 'DamObject'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [damObjectInstance: damObjectInstance]
        }
    }

    def update = {
        def damObjectInstance = DamObject.get(params.id)
        if (damObjectInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (damObjectInstance.version > version) {
                    
                    damObjectInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'damObject.label', default: 'DamObject')] as Object[], "Another user has updated this DamObject while you were editing")
                    render(view: "edit", model: [damObjectInstance: damObjectInstance])
                    return
                }
            }
            damObjectInstance.properties = params
            if (!damObjectInstance.hasErrors() && damObjectInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'damObject.label', default: 'DamObject'), damObjectInstance.id])}"
                redirect(action: "show", id: damObjectInstance.id)
            }
            else {
                render(view: "edit", model: [damObjectInstance: damObjectInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'damObject.label', default: 'DamObject'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def damObjectInstance = DamObject.get(params.id)
        if (damObjectInstance) {
            try {
                damObjectInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'damObject.label', default: 'DamObject'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'damObject.label', default: 'DamObject'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'damObject.label', default: 'DamObject'), params.id])}"
            redirect(action: "list")
        }
    }
}
