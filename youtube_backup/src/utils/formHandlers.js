export const handleInputChange = (e, formData, setFormData) => {
    const { id, value } = e.target;
    setFormData({
        ...formData,
        [id]: value
    });
};

export const handleImageChange = (e, formData, setFormData, setError) => {
    const file = e.target.files[0];
    const validImageTypes = ['image/jpeg', 'image/png', 'image/gif'];

    if (file && !validImageTypes.includes(file.type)) {
        setError('Invalid file type. Please upload an image.');
        setFormData({
            ...formData,
            image: null
        });
    } else {
        const reader = new FileReader();
        reader.onloadend = () => {
            setError('');
            setFormData({
                ...formData,
                image: reader.result // Store the data URL of the image
            });
        };
        reader.readAsDataURL(file);
    }
};
