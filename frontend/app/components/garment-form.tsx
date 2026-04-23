import "./garment-form.css";
import { useState } from "react";
import type GarmentExtendedDTO from "~/dtos/GarmentExtendedDTO";

interface GarmentFormProps {
    garment?: GarmentExtendedDTO;
    formAction: (payload: FormData) => void;
    isPending: boolean;
    error: string | null;
}

export default function GarmentForm({ garment, formAction, isPending, error }: GarmentFormProps) {
    const isEdit = !!garment;
    const [updateImage, setUpdateImage] = useState(false);
    const [validated, setValidated] = useState(false); // For validation

    const handleSubmit = (event: React.SubmitEvent<HTMLFormElement>) => {
        const form = event.currentTarget;

        // If form si not valid
        if (form.checkValidity() === false) {
            event.preventDefault();
            event.stopPropagation();
        }

        setValidated(true);
    };

    return (
        <div className="container">
            <div className="container-header-form">
                <h3>{isEdit ? "Editar prenda" : "Añadir nueva prenda"}</h3>
            </div>

            {/* If there exists a server error */}
            {error && <div className="alert alert-danger mt-3">{error}</div>}

            <hr className="gap-form" />

            <form action={formAction} 
            onSubmit={handleSubmit} // For validation
            className={`needs-validation ${validated ? "was-validated" : ""}`} 
            noValidate
            > 
                {/* Only Id if edition */}
                {garment?.id && <input type="hidden" name="id" value={garment.id} />}

                {/* NAME */}
                <div className="mb-3">
                    <label htmlFor="inputClothName" className="form-label">
                        <b>Nombre</b>
                    </label>
                    <div className="input-group">
                        <span className="input-group-text">
                            <i className="bi bi-alphabet"></i>
                        </span>
                        <input
                        type="text"
                        className="form-control"
                        id="inputClothName"
                        name="name"
                        placeholder="Nombre de la prenda"
                        defaultValue={garment?.name || ""}
                        minLength={4}
                        maxLength={100}
                        required
                        />
                        <div className="invalid-feedback" id="titleMesssage">
                            El nombre no puede estar vacío y debe tener entre 4 y 100 caracteres.
                        </div>
                    </div>
                </div>

                {/* CATEGORY */}
                <div className="mb-3">
                    <label htmlFor="inputCategory" className="form-label">
                        <b>Categoría</b>
                    </label>
                    <div className="input-group">
                        <span className="input-group-text">
                            <i className="bi bi-tag"></i>
                        </span>
                        <select
                        className="form-select"
                        id="inputCategory"
                        name="category"
                        required
                        defaultValue={garment?.category || ""}
                        >
                            {!garment && <option value="" disabled>Selecciona categoría</option>}
                            <option value="Camisas">Camisas</option>
                            <option value="Pantalones">Pantalones</option>
                            <option value="Zapatos">Zapatos</option>
                            <option value="Chaquetas">Chaquetas</option>
                            <option value="Accesorios">Accesorios</option>
                            <option value="Otros">Otros</option>
                        </select>
                        <div className="invalid-feedback">La categoría no puede estar vacía.</div>
                    </div>
                </div>

                {/* PRICE */}
                <div className="mb-3">
                    <label htmlFor="inputClothPrice" className="form-label">
                        <b>Precio</b>
                    </label>
                    <div className="input-group">
                        <span className="input-group-text">
                            <i className="bi bi-cash-coin"></i>
                        </span>
                        <input
                        type="number"
                        className="form-control"
                        id="inputClothPrice"
                        name="price"
                        step="0.01"
                        placeholder="Precio de la prenda"
                        defaultValue={garment?.price}
                        min="1"
                        max="6000"
                        required
                        />
                        <span className="input-group-text" id="input-group-price">€</span>
                        <div className="invalid-feedback">El precio debe ser un número entre 1 y 6000.</div>
                    </div>
                </div>

                {/* IMAGE SECTION */}
                <div className="mb-3">
                    <label htmlFor="inputClothImage" className="form-label">
                        <b>Imagen</b>
                    </label>

                    {isEdit && garment?.image && (
                        <>
                        <div className="mb-3">
                            <img
                            src={`/api/v1/images/${garment.image.id}/media`}
                            alt={garment.name}
                            className="img-fluid mb-2"
                            style={{ maxWidth: "200px" }}
                            />
                        </div>

                        <div className="form-check mb-2">
                            <input
                            className="form-check-input"
                            type="checkbox"
                            id="updateImage"
                            name="updateImage"
                            checked={updateImage}
                            onChange={(e) => setUpdateImage(e.target.checked)}
                            />
                            <label className="form-check-label" htmlFor="updateImage">
                                Actualizar imagen
                            </label>
                        </div>
                        </>
                    )}

                    <div className="input-group">
                        <span className="input-group-text">
                            <i className="bi bi-image"></i>
                        </span>
                        <input
                        type="file"
                        className="form-control"
                        id="inputClothImage"
                        name="imageFile"
                        accept=".jpg,.jpeg,.png"
                        required={!isEdit || updateImage}
                        disabled={isEdit && !updateImage}
                        />
                        <div className="invalid-feedback">
                            {isEdit
                                ? 'Si no quieres actualizar la imagen, desmarca la casilla "Actualizar imagen".'
                                : 'La imagen no puede estar vacía.'}
                        </div>
                    </div>
                </div>

                {/* DESCRIPTION */}
                <div className="mb-3">
                    <label htmlFor="inputClothDescription" className="form-label">
                        <b>Descripción</b>
                    </label>
                    <div className="input-group">
                        <span className="input-group-text">
                            <i className="bi bi-list-ul"></i>
                        </span>
                        <textarea
                        className="form-control"
                        id="inputClothDescription"
                        name="description"
                        placeholder="Descripción de la prenda"
                        rows={5}
                        minLength={3}
                        maxLength={200}
                        defaultValue={garment?.description || ""}
                        required
                        ></textarea>
                        <div className="invalid-feedback">La descripción debe tener entre 3 y 200 caracteres.</div>
                    </div>
                </div>

                {/* FEATURES */}
                <div className="mb-3">
                    <label htmlFor="inputClothFeatures" className="form-label">
                        <b>Características</b>
                    </label>
                    <div className="input-group">
                        <span className="input-group-text">
                            <i className="bi bi-list-ul"></i>
                        </span>
                        <textarea
                        className="form-control"
                        id="inputClothFeatures"
                        name="features"
                        placeholder="Características de la prenda"
                        rows={5}
                        minLength={5}
                        maxLength={300}
                        defaultValue={garment?.features || ""}
                        required
                        ></textarea>
                        <div className="invalid-feedback">Las características deben tener entre 5 y 300 caracteres.</div>
                    </div>
                </div>

                <div className="mt-4">
                    <button type="submit" className="btn btn-success text-dark" disabled={isPending}>
                        {isPending ? (
                        <>
                            <span className="spinner-border spinner-border-sm me-2"></span>
                            Guardando...
                        </>
                        ) : (
                        <>Guardar <i className="bi bi-check-lg"></i></>
                        )}
                    </button>
                </div>
            </form>
        </div>
    );
}