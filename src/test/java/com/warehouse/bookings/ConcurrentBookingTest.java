package com.warehouse.bookings;

import com.warehouse.BaseIntegrationTest;
import com.warehouse.bookings.repository.BookingsRepository;
import com.warehouse.bookings.service.BookingService;
import com.warehouse.common.dto.CreateBooking;
import com.warehouse.common.exceptions.BookingConflictException;
import com.warehouse.customers.entity.Customer;
import com.warehouse.customers.repository.CustomersRepository;
import com.warehouse.storage.entity.StorageStatus;
import com.warehouse.storage.entity.StorageUnit;
import com.warehouse.storage.repository.StorageRepository;
import com.warehouse.tenants.entity.Tenant;
import com.warehouse.tenants.repository.TenantRepository;
import com.warehouse.warehouses.entity.Warehouse;
import com.warehouse.warehouses.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrentBookingTest extends BaseIntegrationTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private BookingsRepository bookingsRepository;

    private UUID tenantId;
    private UUID storageUnitId;
    private UUID customer1Id;
    private UUID customer2Id;

    @BeforeEach
    @Transactional
    void setUp() {
        bookingsRepository.deleteAll();
        customersRepository.deleteAll();
        storageRepository.deleteAll();
        warehouseRepository.deleteAll();
        tenantRepository.deleteAll();

        Tenant tenant = new Tenant();
        tenant.setCompanyName("Test Tenant");
        tenant.setEmail("test@tenant.com");
        tenant = tenantRepository.save(tenant);
        tenantId = tenant.getId();

        Warehouse warehouse = new Warehouse();
        warehouse.setTenantId(tenantId);
        warehouse.setName("Test Warehouse");
        warehouse.setLocation("Test Location");
        warehouse = warehouseRepository.save(warehouse);

        StorageUnit unit = new StorageUnit();
        unit.setWarehouseId(warehouse.getId());
        unit.setUnitNumber("UNIT-001");
        unit.setCapacityKg(100);
        unit.setStatus(StorageStatus.AVAILABLE);
        unit = storageRepository.save(unit);
        storageUnitId = unit.getId();

        Customer customer1 = new Customer();
        customer1.setTenantId(tenantId);
        customer1.setCompanyName("Customer 1");
        customer1.setContactEmail("customer1@test.com");
        customer1 = customersRepository.save(customer1);
        customer1Id = customer1.getId();

        Customer customer2 = new Customer();
        customer2.setTenantId(tenantId);
        customer2.setCompanyName("Customer 2");
        customer2.setContactEmail("customer2@test.com");
        customer2 = customersRepository.save(customer2);
        customer2Id = customer2.getId();
    }

    @Test
    void shouldPreventDoubleBooking_whenTwoCustomersBookSimultaneously() throws InterruptedException {
        CreateBooking booking1 = new CreateBooking(
                customer1Id,
                storageUnitId,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                BigDecimal.valueOf(100)
        );

        CreateBooking booking2 = new CreateBooking(
                customer2Id,
                storageUnitId,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                BigDecimal.valueOf(100)
        );

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(2);
        CountDownLatch endLatch = new CountDownLatch(2);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        Runnable bookTask1 = () -> {
            startLatch.countDown();
            try {
                startLatch.await();
                bookingService.createBooking(tenantId, booking1);
                successCount.incrementAndGet();
            } catch (BookingConflictException | IllegalStateException e) {
                conflictCount.incrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                endLatch.countDown();
            }
        };

        Runnable bookTask2 = () -> {
            startLatch.countDown();
            try {
                startLatch.await();
                bookingService.createBooking(tenantId, booking2);
                successCount.incrementAndGet();
            } catch (BookingConflictException | IllegalStateException e) {
                conflictCount.incrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                endLatch.countDown();
            }
        };

        executor.submit(bookTask1);
        executor.submit(bookTask2);

        endLatch.await();
        executor.shutdown();

        System.out.println("Success count: " + successCount.get());
        System.out.println("Conflict count: " + conflictCount.get());
        System.out.println("Total bookings in DB: " + bookingsRepository.count());

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(conflictCount.get()).isEqualTo(1);
        assertThat(bookingsRepository.count()).isEqualTo(1);

        StorageUnit unit = storageRepository.findById(storageUnitId).orElseThrow();
        assertThat(unit.getStatus()).isEqualTo(StorageStatus.OCCUPIED);
    }
}
