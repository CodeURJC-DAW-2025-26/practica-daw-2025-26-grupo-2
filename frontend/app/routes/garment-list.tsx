import { Link, useSearchParams } from "react-router";
import { useState, useEffect } from "react";
import type { Route } from "./+types/home";
import { getGarments, getOffers } from "~/services/garments-service";
import { useUserStore } from "~/stores/user-store";
import type GarmentBasicDTO from "~/dtos/GarmentBasicDTO";
import GarmentCard from "~/components/garment-card";
import GarmentFilters from "~/components/garmentFilter-form";
import { Container, Row, Col, Button, Spinner } from "react-bootstrap";

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

    function handleFilterSubmit(event: React.SubmitEvent) {
        event.preventDefault();
        const formData = new FormData(event.target);
        const params = Object.fromEntries(formData.entries());

        const cleanParams = Object.fromEntries(
            Object.entries(params).filter(([_, v]) => v !== "")
        );

        setSearchParams(cleanParams as any);
    }

    async function handleLoadMore() {
        setIsLoadingMore(true);
        try {
            const newGarments = await getGarments(
                searchParams.get("nameSearch") || "",
                searchParams.get("categorySearch") || "",
                Number(searchParams.get("minPrice")) || 0,
                Number(searchParams.get("maxPrice")) || 1000,
                searchParams.get("sort") || "id",
                page,
                PAGE_SIZE
            );

            if (newGarments.length > 0) {
                setGarments((prev) => [...prev, ...newGarments]);
                setPage(page + 1);
                setHasMore(newGarments.length === PAGE_SIZE);
            } else {
                setHasMore(false);
            }
        } catch (err) {
            console.error("Error al cargar más prendas", err);
        } finally {
            setIsLoadingMore(false);
        }
    }

    return (
        <>
            {/* RECOMMENDATIONS */}
            {isUser && (
                <section className="offers-section py-5">
                    <Container className="container-main-offers">
                        <div className="d-flex align-items-center justify-content-center mb-5 container-offers-title">
                            <hr className="flex-grow-1 d-none d-sm-block border-2" />
                            <h2 className="text-center mx-4 fw-bold mb-0 text-uppercase">
                                Recomendaciones para ti
                            </h2>
                            <hr className="flex-grow-1 d-none d-sm-block border-2" />
                        </div>

                        <Row id="offers-list">
                            {offers.map((offer) => (
                                <Col key={`offer-${offer.id}`} xs={12} sm={6} md={4} lg={3} className="garment-item">
                                    <GarmentCard garment={offer} />
                                </Col>
                            ))}
                        </Row>
                    </Container>
                </section>
            )}

            {/* MAIN */}
            <Container className="container-main py-5 px-4">
                <div className="position-relative d-flex align-items-center justify-content-center mb-4">

                    {isAdmin && (
                        <Button
                            as={Link as any}
                            to="/garment/new"
                            size="sm"
                            className="position-absolute start-0 d-flex align-items-center gap-2"
                        >
                            Añadir prenda <i className="bi bi-plus-circle"></i>
                        </Button>
                    )}

                    <h2 className="title mb-0">Artículos</h2>

                    <Button
                        variant="outline-dark"
                        size="sm"
                        className="position-absolute end-0 d-flex align-items-center gap-2 text-uppercase fw-bold"
                        onClick={() => setShowFilters(!showFilters)}
                    >
                        <i className="bi bi-sliders"></i>
                        <span className="d-none d-sm-inline">Filtrar</span>
                    </Button>
                </div>

                {/* FILTERS */}
                <GarmentFilters
                    onFilter={handleFilterSubmit}
                    show={showFilters}
                    currentParams={searchParams}
                />

                {/* LIST OF GARMENTS */}
                <Row id="garments-list">
                    {garments.map((garment) => (
                        <Col key={garment.id} xs={12} sm={6} md={4} lg={3} className="garment-item">
                            <GarmentCard garment={garment} />
                        </Col>
                    ))}
                </Row>
            </Container>

            {/* LOAD MORE */}
            <div className="text-center mt-4 mb-5">
                {isLoadingMore ? (
                    <Spinner animation="border" role="status">
                        <span className="visually-hidden">Cargando...</span>
                    </Spinner>
                ) : (
                    hasMore && (
                        <Button onClick={handleLoadMore}>
                            Cargar más <i className="bi bi-plus-circle"></i>
                        </Button>
                    )
                )}
            </div>
        </>
    );
}