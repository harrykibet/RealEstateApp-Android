package com.estatia.realestate.apps.core.model.service

enum class ServiceType {
    // General Maintenance and Cleaning
    MAID,
    CLEANING_SERVICE,
    PLUMBER,
    ELECTRICIAN,
    GARDENER,
    HANDYMAN, // General repairs and maintenance
    PEST_CONTROL, // Pest extermination services

    // Construction and Renovation
    INTERIOR_DESIGN,
    ARCHITECT,
    CONSTRUCTION_WORKER, // General construction services
    PAINTER, // Painting services
    ROOFING_SPECIALIST, // Roof repairs and installations
    TILER, // Tile installations and repairs
    FLOORING_EXPERT, // Floor installations and refinishing
    CARPENTER, // Custom furniture or woodwork
    RENOVATION_CONTRACTOR, // Full renovation services

    // Security and Safety
    SECURITY_SERVICE,
    CCTV_INSTALLER, // Surveillance camera installations
    FIRE_SAFETY_SPECIALIST, // Fire extinguisher and fire safety compliance
    LOCKSMITH, // Key and lock-related services

    // Moving and Relocation
    RELOCATION_MOVER,
    STORAGE_PROVIDER, // Temporary storage services

    // Legal and Financial
    INSURANCE,
    PROPERTY_LAWYER, // Legal services for property purchases or disputes
    REAL_ESTATE_AGENT, // Realtors for property buying/selling
    MORTGAGE_ADVISOR, // Loan and mortgage advice

    // Utilities
    WATER_SUPPLY_SPECIALIST, // Borehole drilling or water supply setup
    SOLAR_INSTALLER, // Solar panel installations
    HVAC_TECHNICIAN, // Heating, ventilation, and air conditioning services

    // Aesthetic and Outdoor Services
    LANDSCAPER, // Landscaping and outdoor beautification
    POOL_MAINTENANCE, // Swimming pool cleaning and repairs
    FENCING_CONTRACTOR, // Fencing installations and repairs

    // Special Services
    HOME_AUTOMATION_SPECIALIST, // Smart home setups (IoT devices)
    APPLIANCE_REPAIR_TECHNICIAN, // Repairs for home appliances
    EVENT_PLANNER, // Event planning in properties (e.g., open houses or events)
    WASTE_DISPOSAL, // Garbage collection or waste management services

    // Default
    UNKNOWN // Default case for unclassified services
}

