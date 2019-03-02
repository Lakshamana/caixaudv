/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { CantinadoreiTestModule } from '../../../test.module';
import { CompraDeleteDialogComponent } from 'app/entities/compra/compra-delete-dialog.component';
import { CompraService } from 'app/entities/compra/compra.service';

describe('Component Tests', () => {
    describe('Compra Management Delete Component', () => {
        let comp: CompraDeleteDialogComponent;
        let fixture: ComponentFixture<CompraDeleteDialogComponent>;
        let service: CompraService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CantinadoreiTestModule],
                declarations: [CompraDeleteDialogComponent]
            })
                .overrideTemplate(CompraDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(CompraDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CompraService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});