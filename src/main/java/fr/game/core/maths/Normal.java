package fr.game.core.maths;

import org.joml.Vector3f;

public class Normal {

        private Vector3f normal;

        public Normal(float x, float y, float z) {
            this.normal = new Vector3f(x, y, z);
        }

        public Normal(Vector3f normal) {
            this.normal = normal;
        }

        public Vector3f getNormal() {
            return normal;
        }

}
