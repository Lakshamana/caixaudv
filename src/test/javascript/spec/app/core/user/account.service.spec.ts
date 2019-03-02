import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SERVER_API_URL } from 'app/app.constants';
import { AccountService, JhiTrackerService } from 'app/core';
import { JhiDateUtils, JhiLanguageService } from 'ng-jhipster';
import { SessionStorageService } from 'ngx-webstorage';
import { MockLanguageService } from '../../../helpers/mock-language.service';
import { MockTrackerService } from '../../../helpers/mock-tracker.service';

describe('Service Tests', () => {
    describe('Account Service', () => {
        let service: AccountService;
        let httpMock;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule],
                providers: [
                    JhiDateUtils,
                    SessionStorageService,
                    {
                        provide: JhiLanguageService,
                        useClass: MockLanguageService
                    },
                    {
                        provide: JhiTrackerService,
                        useClass: MockTrackerService
                    }
                ]
            });

            service = TestBed.get(AccountService);
            httpMock = TestBed.get(HttpTestingController);
        });

        afterEach(() => {
            httpMock.verify();
        });

        describe('Service methods', () => {
            it('should call /account if user is undefined', () => {
                service.identity().then(() => {});
                const req = httpMock.expectOne({ method: 'GET' });
                const resourceUrl = SERVER_API_URL + 'api/account';

                expect(req.request.url).toEqual(`${resourceUrl}`);
            });

            it('should call /account only once', () => {
                service.identity().then(() => service.identity().then(() => {}));
                const req = httpMock.expectOne({ method: 'GET' });
                const resourceUrl = SERVER_API_URL + 'api/account';

                expect(req.request.url).toEqual(`${resourceUrl}`);
                req.flush({
                    firstName: 'John'
                });
            });

            describe('hasAuthority', () => {
                it('should return false if user is not logged', async () => {
                    const hasAuthority = await service.hasAuthority('ROLE_OPERATOR');
                    expect(hasAuthority).toBeFalsy();
                });

                it('should return false if user is logged and has not authority', async () => {
                    service.authenticate({
                        authorities: ['ROLE_OPERATOR']
                    });

                    const hasAuthority = await service.hasAuthority('ROLE_ADMIN');

                    expect(hasAuthority).toBeFalsy();
                });

                it('should return true if user is logged and has authority', async () => {
                    service.authenticate({
                        authorities: ['ROLE_OPERATOR']
                    });

                    const hasAuthority = await service.hasAuthority('ROLE_OPERATOR');

                    expect(hasAuthority).toBeTruthy();
                });
            });

            describe('hasAnyAuthority', () => {
                it('should return false if user is not logged', async () => {
                    const hasAuthority = await service.hasAnyAuthority(['ROLE_OPERATOR']);
                    expect(hasAuthority).toBeFalsy();
                });

                it('should return false if user is logged and has not authority', async () => {
                    service.authenticate({
                        authorities: ['ROLE_OPERATOR']
                    });

                    const hasAuthority = await service.hasAnyAuthority(['ROLE_ADMIN']);

                    expect(hasAuthority).toBeFalsy();
                });

                it('should return true if user is logged and has authority', async () => {
                    service.authenticate({
                        authorities: ['ROLE_OPERATOR']
                    });

                    const hasAuthority = await service.hasAnyAuthority(['ROLE_OPERATOR', 'ROLE_ADMIN']);

                    expect(hasAuthority).toBeTruthy();
                });
            });
        });
    });
});
