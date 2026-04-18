import { Link, useSearchParams } from "react-router";
import { useState, useEffect } from "react";
import type { Route } from "./+types/home";
import { getGarments, getOffers } from "~/services/garments-service";
import { useUserStore } from "~/stores/user-store";
import type GarmentBasicDTO from "~/dtos/GarmentBasicDTO";
import GarmentCard from "~/components/garment-card";
import GarmentFilters from "~/components/garmentFilter-form";

const PAGE_SIZE = 10;

interface GarmentLoaderData {
    garments: GarmentBasicDTO[];
    offers: GarmentBasicDTO[];
    userId: number | null;
    isAdmin: boolean;
    hasMore: boolean;
}

export async function clientLoader({ request }: Route.ClientLoaderArgs) {
    const url = new URL(request.url);
    const searchParams = url.searchParams;
    // We can't use the hook here, so we directly access the store's state and methods
    // TODO: Ask about a better way to handle this, maybe with a custom hook that can be used in loaders?
    await useUserStore.getState().loadLoggedUser();
    const { user } = useUserStore.getState();

    const isAdmin = user?.roles?.includes("ADMIN") ?? false;
    const isUser = !!user;
    const userId = user?.id ?? null;

    const garments = await getGarments(
                searchParams.get("nameSearch") || "",
                searchParams.get("categorySearch") || "",
                Number(searchParams.get("minPrice")) || 0,
                Number(searchParams.get("maxPrice")) || 1000,
                searchParams.get("sort") || "id",
                0, PAGE_SIZE
            );

    if (isUser) { 
        const offers = await getOffers();
        return { garments, offers, userId, isAdmin, hasMore: garments.length === PAGE_SIZE };
    }
    return { garments, offers: [], userId: null, isAdmin, hasMore: garments.length === PAGE_SIZE };
}

export default function GarmentList({ loaderData }: Route.ComponentProps) {

    const data = loaderData as unknown as GarmentLoaderData;

    const [garments, setGarments] = useState<GarmentBasicDTO[]>(data.garments);
    const [hasMore, setHasMore] = useState(data.hasMore);
    const [page, setPage] = useState(1);
    const [isLoadingMore, setIsLoadingMore] = useState(false);
    const [showFilters, setShowFilters] = useState(false);
    const [searchParams, setSearchParams] = useSearchParams();

    const offers = data.offers;
    const userId = data.userId;
    const isUser = userId !== null;
    const isAdmin = data.isAdmin;

    useEffect(() => {
        setGarments(data.garments);
        setHasMore(data.hasMore);
        setPage(1);
    }, [data]);

    function handleFilterSubmit (event: React.SubmitEvent){
        event.preventDefault();
        const formData = new FormData(event.target);
        const params = Object.fromEntries(formData.entries());

        const cleanParams = Object.fromEntries(
        Object.entries(params).filter(([_, v]) => v !== "")
        );
        setSearchParams(cleanParams as any);
    };

    async function handleLoadMore (){
        setIsLoadingMore(true);
        try {
            const newGarments = await getGarments(
                searchParams.get("nameSearch") || "",
                searchParams.get("categorySearch") || "",
                Number(searchParams.get("minPrice")) || 0,
                Number(searchParams.get("maxPrice")) || 1000,
                searchParams.get("sort") || "id",
                page, PAGE_SIZE
            )
            if (newGarments.length > 0){
                setGarments((prev)=>[...prev, ...newGarments]);
                setPage(page+1);
                setHasMore(newGarments.length === 10);
            } else {
                setHasMore(false);
            }
        } catch(err){
            console.error("Error al cargar más prendas", err);
        } finally {
            setIsLoadingMore(false);
        }
    }

    return (
        <>
            {/* RECOMMENDATIONS for loaded users */}
            {isUser && (
                <section className="offers-section py-5">
                <div className="container container-main-offers">
                    <div className="d-flex align-items-center justify-content-center mb-5 container-offers-title">
                    <hr className="flex-grow-1 d-none d-sm-block border-2" />
                    <h2 className="text-center mx-4 fw-bold mb-0 text-uppercase">Recomendaciones para ti</h2>
                    <hr className="flex-grow-1 d-none d-sm-block border-2" />
                    </div>
                    <div className="row" id="offers-list">
                    {offers.map((offer) => (
                        <div key={`offer-${offer.id}`} className="col-xs-12 col-sm-6 col-md-4 col-lg-3 garment-item">
                        <GarmentCard garment={offer} />
                        </div>
                    ))}
                    </div>
                </div>
                </section>
            )}

            {/* MAIN SECTION */}
            <main className="container py-5 px-4">
                <div className="container container-main">
                <div className="position-relative d-flex align-items-center justify-content-center mb-4">
                    {isAdmin && (
                    <Link to="/garment/new" className="btn btn-primary btn-sm position-absolute start-0 d-flex align-items-center gap-2">
                        Añadir prenda <i className="bi bi-plus-circle"></i>
                    </Link>
                    )}

                    <h2 className="title mb-0">Artículos</h2>

                    <button
                    className="btn btn-outline-dark btn-sm position-absolute end-0 d-flex align-items-center gap-2 text-uppercase fw-bold"
                    type="button"
                    onClick={() => setShowFilters(!showFilters)}
                    >
                    <i className="bi bi-sliders"></i>
                    <span className="d-none d-sm-inline">Filtrar</span>
                    </button>
                </div>

                {/* FILTERS */}
                <GarmentFilters 
                onFilter={handleFilterSubmit} 
                show={showFilters} 
                currentParams={searchParams} 
                />

                {/* LIST OF GARMENTS */}
                <div className="row" id="garments-list">
                    {garments.map((garment) => (
                    <div key={garment.id} className="col-xs-12 col-sm-6 col-md-4 col-lg-3 garment-item">
                        <GarmentCard garment={garment} />
                    </div>
                    ))}
                </div>
                </div>
            </main>

            {/* (Load more) */}
            <div className="text-center mt-4 mb-5">
                {isLoadingMore ? (
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Cargando...</span>
                </div>
                ) : (
                hasMore && (
                    <button className="btn btn-primary" type="button" onClick={handleLoadMore}>
                    Cargar más <i className="bi bi-plus-circle"></i>
                    </button>
                )
                )}
            </div>
            </>
    );
}
